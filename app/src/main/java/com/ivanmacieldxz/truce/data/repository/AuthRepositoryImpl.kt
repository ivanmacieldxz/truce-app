package com.ivanmacieldxz.truce.data.repository

import com.ivanmacieldxz.truce.data.remote.BackendApiService
import com.ivanmacieldxz.truce.domain.repository.AuthRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import javax.inject.Inject

import com.ivanmacieldxz.truce.domain.repository.AuthException
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.exceptions.HttpRequestException

class AuthRepositoryImpl @Inject constructor(
    private val supabaseClient: SupabaseClient,
    private val apiService: BackendApiService
) : AuthRepository {

    override fun isUserLoggedIn(): Flow<Boolean?> {
        return supabaseClient.auth.sessionStatus.map { status ->
            when (status) {
                is SessionStatus.Initializing -> null
                is SessionStatus.Authenticated -> true
                else -> false
            }
        }
    }

    override suspend fun login(email: String, password: String): Result<String> {
        return try {
            supabaseClient.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            // Trigger backend getMyProfile to ensure user is synced
            try {
                apiService.getMyProfile()
            } catch (e: Exception) {
                // If backend fails, we must sign out to prevent false-positive login state
                try { supabaseClient.auth.signOut() } catch (ignored: Exception) {}
                throw e
            }
            Result.success("Sesión iniciada correctamente")
        } catch (e: Exception) {
            Result.failure(mapException(e))
        }
    }

    override suspend fun signUp(email: String, password: String, username: String, fullName: String): Result<String> {
        return try {
            try {
                supabaseClient.auth.signOut()
            } catch (e: Exception) {
                // Ignore signout errors
            }

            val result = supabaseClient.auth.signUpWith(Email) {
                this.email = email
                this.password = password
                // Passing the username and full_name to user_metadata
                this.data = buildJsonObject {
                    put("username", username)
                    put("full_name", fullName)
                }
            }
            
            // If email confirmations are enabled, current session will be null
            if (supabaseClient.auth.currentAccessTokenOrNull() != null) {
                apiService.getMyProfile()
                Result.success("Registro exitoso")
            } else {
                Result.success("Por favor, revisá tu casilla de correo para confirmar el registro antes de iniciar sesión.")
            }
        } catch (e: Exception) {
            Result.failure(mapException(e))
        }
    }

    override suspend fun logout(): Result<Unit> {
        return try {
            supabaseClient.auth.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(mapException(e))
        }
    }

    private fun mapException(e: Exception): Exception {
        return when (e) {
            is retrofit2.HttpException -> {
                if (e.code() == 401) {
                    AuthException(
                        userMessage = "Ocurrió un problema de autenticación con el servidor.",
                        debugMessage = "Error HTTP 401: El token de sesión no es válido o el backend de Render no lo reconoció. Verificá SUPABASE_JWT_SECRET en tu servidor."
                    )
                } else {
                    AuthException(
                        userMessage = "Hubo un problema de conexión con el servidor. Intentá de nuevo.",
                        debugMessage = "Error del Servidor al obtener el perfil: HTTP ${e.code()}"
                    )
                }
            }
            is RestException -> {
                val errorMsg = e.error.lowercase()
                val description = e.description?.lowercase() ?: ""
                val fullError = "$errorMsg $description"
                
                val userFriendlyMessage = when {
                    fullError.contains("invalid login credentials") -> "Credenciales inválidas. Verificá tu correo y contraseña."
                    fullError.contains("user already registered") -> "Ya existe un usuario con este correo."
                    fullError.contains("password should be at least") -> "La contraseña debe tener al menos 6 caracteres."
                    fullError.contains("rate limit") -> "Demasiados intentos. Por favor, intentá más tarde."
                    fullError.contains("email link is invalid") || fullError.contains("token has expired") -> "El enlace es inválido o expiró."
                    else -> "Ocurrió un error al procesar tu solicitud."
                }
                
                AuthException(
                    userMessage = userFriendlyMessage,
                    debugMessage = "Supabase RestException: ${e.message}"
                )
            }
            is HttpRequestException -> {
                AuthException(
                    userMessage = "No se pudo conectar. Verificá tu conexión a internet.",
                    debugMessage = "Supabase HttpRequestException: ${e.message}"
                )
            }
            is AuthException -> e
            else -> AuthException(
                userMessage = "Ocurrió un error inesperado.",
                debugMessage = "Exception: ${e.message}"
            )
        }
    }
}
