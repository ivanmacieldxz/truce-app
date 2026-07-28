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
            apiService.getMyProfile()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signUp(email: String, password: String, username: String): Result<Unit> {
        return try {
            supabaseClient.auth.signUpWith(Email) {
                this.email = email
                this.password = password
                // Passing the username to user_metadata
                this.data = buildJsonObject {
                    put("username", username)
                }
            }
            // Trigger backend getMyProfile to ensure user is synced and created
            apiService.getMyProfile()
            Result.success(Unit)
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
