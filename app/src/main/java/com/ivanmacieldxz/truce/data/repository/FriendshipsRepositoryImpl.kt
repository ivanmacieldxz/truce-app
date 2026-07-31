package com.ivanmacieldxz.truce.data.repository

import com.ivanmacieldxz.truce.data.remote.BackendApiService
import com.ivanmacieldxz.truce.data.remote.CreateFriendshipDto
import com.ivanmacieldxz.truce.data.remote.FriendDto
import com.ivanmacieldxz.truce.data.remote.FriendshipRequestDto
import com.ivanmacieldxz.truce.data.remote.UpdateFriendshipDto
import com.ivanmacieldxz.truce.data.remote.UserSummaryDto
import com.ivanmacieldxz.truce.domain.repository.FriendshipsRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import retrofit2.HttpException
import javax.inject.Inject

class FriendshipsRepositoryImpl @Inject constructor(
    private val apiService: BackendApiService,
    private val supabaseClient: SupabaseClient
) : FriendshipsRepository {

    override suspend fun searchUsers(query: String, page: Int, limit: Int): Result<List<UserSummaryDto>> {
        return try {
            val response = apiService.searchUsers(query, page, limit)
            val currentUserId = supabaseClient.auth.currentUserOrNull()?.id
            val filteredResponse = if (currentUserId != null) {
                response.filter { it.id != currentUserId }
            } else {
                response
            }
            Result.success(filteredResponse)
        } catch (e: Exception) {
            Result.failure(mapException(e))
        }
    }

    override suspend fun getFriends(page: Int, limit: Int): Result<List<FriendDto>> {
        return try {
            val response = apiService.getFriends(page, limit)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(mapException(e))
        }
    }

    override suspend fun getFriendRequests(type: String?, page: Int, limit: Int): Result<List<FriendshipRequestDto>> {
        return try {
            val response = apiService.getFriendRequests(type, page, limit)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(mapException(e))
        }
    }

    override suspend fun sendFriendRequest(targetUserId: String): Result<Unit> {
        return try {
            apiService.sendFriendRequest(CreateFriendshipDto(targetUserId))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(mapException(e))
        }
    }

    override suspend fun respondToFriendRequest(requestId: String, accept: Boolean): Result<Unit> {
        return try {
            val status = if (accept) "ACCEPTED" else "REJECTED"
            apiService.respondToFriendRequest(requestId, UpdateFriendshipDto(status))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(mapException(e))
        }
    }

    override suspend fun removeFriend(friendId: String): Result<Unit> {
        return try {
            apiService.removeFriend(friendId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(mapException(e))
        }
    }

    private fun mapException(e: Exception): Exception {
        return when (e) {
            is HttpException -> {
                when (e.code()) {
                    409 -> Exception("Ya existe una relación o solicitud con este usuario.")
                    404 -> Exception("No se encontró el recurso solicitado.")
                    400 -> Exception("Petición inválida. Verifica los datos.")
                    else -> Exception("Error del servidor: HTTP ${e.code()}")
                }
            }
            else -> Exception("Ocurrió un error inesperado. Revisa tu conexión a internet.")
        }
    }
}
