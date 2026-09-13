package com.ivanmacieldxz.truce.data.repository

import com.ivanmacieldxz.truce.data.remote.BackendApiService
import com.ivanmacieldxz.truce.data.remote.UpdateEmailDto
import com.ivanmacieldxz.truce.data.remote.UpdateUsernameDto
import com.ivanmacieldxz.truce.data.remote.UserDto
import com.ivanmacieldxz.truce.domain.repository.UserRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import retrofit2.HttpException
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val apiService: BackendApiService,
    private val supabaseClient: SupabaseClient
) : UserRepository {

    override suspend fun getMyProfile(): Result<UserDto> {
        return try {
            val profile = apiService.getMyProfile()
            Result.success(profile)
        } catch (e: Exception) {
            Result.failure(mapException(e))
        }
    }

    override suspend fun updateUsername(username: String): Result<UserDto> {
        return try {
            val updatedUser = apiService.updateUsername(UpdateUsernameDto(username))
            Result.success(updatedUser)
        } catch (e: Exception) {
            Result.failure(mapException(e))
        }
    }

    override suspend fun updateEmail(email: String): Result<UserDto> {
        return try {
            val updatedUser = apiService.updateEmail(UpdateEmailDto(email))
            Result.success(updatedUser)
        } catch (e: Exception) {
            Result.failure(mapException(e))
        }
    }

    override suspend fun deleteAccount(): Result<Unit> {
        return try {
            apiService.deleteAccount()
            try {
                supabaseClient.auth.signOut()
            } catch (ignored: Exception) {}
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(mapException(e))
        }
    }

    private fun mapException(e: Exception): Exception {
        return when (e) {
            is HttpException -> {
                when (e.code()) {
                    400 -> Exception("La solicitud contiene datos inválidos.")
                    401 -> Exception("Sesión expirada o no autorizada.")
                    409 -> Exception("El nombre de usuario o correo ya se encuentra en uso.")
                    500 -> Exception("Error en el servidor o al sincronizar con el proveedor de autenticación.")
                    else -> Exception("Error del servidor: HTTP ${e.code()}")
                }
            }
            else -> Exception("Ocurrió un error inesperado. Revisa tu conexión a internet.")
        }
    }
}
