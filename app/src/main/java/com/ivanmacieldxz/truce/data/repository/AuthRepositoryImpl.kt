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

class AuthRepositoryImpl @Inject constructor(
    private val supabaseClient: SupabaseClient,
    private val apiService: BackendApiService
) : AuthRepository {

    override fun isUserLoggedIn(): Flow<Boolean> {
        return supabaseClient.auth.sessionStatus.map { status ->
            status is SessionStatus.Authenticated
        }
    }

    override suspend fun login(email: String, password: String): Result<Unit> {
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
            Result.success(Unit)
        } catch (e: retrofit2.HttpException) {
            if (e.code() == 401) {
                Result.failure(Exception("Error 401: El token de sesión no es válido o el backend de Render no lo reconoció. Verificá SUPABASE_JWT_SECRET en tu servidor."))
            } else {
                Result.failure(Exception("Error del Servidor al obtener el perfil: HTTP ${e.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signUp(email: String, password: String, username: String): Result<Unit> {
        return try {
            try {
                supabaseClient.auth.signOut()
            } catch (e: Exception) {
                // Ignore signout errors
            }

            val result = supabaseClient.auth.signUpWith(Email) {
                this.email = email
                this.password = password
                // Passing the username to user_metadata
                this.data = buildJsonObject {
                    put("username", username)
                }
            }
            
            // If email confirmations are enabled, current session will be null
            if (supabaseClient.auth.currentAccessTokenOrNull() != null) {
                apiService.getMyProfile()
                Result.success(Unit)
            } else {
                // Return a specific error/message to tell user to check email
                Result.failure(Exception("Por favor, revisá tu casilla de correo para confirmar el registro antes de iniciar sesión."))
            }
        } catch (e: retrofit2.HttpException) {
            if (e.code() == 401) {
                Result.failure(Exception("Error de Autenticación con el Backend (HTTP 401). Verificá que el SUPABASE_JWT_SECRET en tu backend de Render coincida con el de este proyecto."))
            } else {
                Result.failure(Exception("Error del Servidor: HTTP ${e.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout(): Result<Unit> {
        return try {
            supabaseClient.auth.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
