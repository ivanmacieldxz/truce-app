package com.ivanmacieldxz.truce.domain.repository

import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun isUserLoggedIn(): Flow<Boolean?>
    suspend fun login(email: String, password: String): Result<String>
    suspend fun signUp(email: String, password: String, username: String, fullName: String): Result<String>
    suspend fun logout(): Result<Unit>
    suspend fun checkUsernameAvailability(username: String): Result<Boolean>
    suspend fun checkEmailAvailability(email: String): Result<Boolean>
}
