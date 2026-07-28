package com.ivanmacieldxz.truce.domain.repository

import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun isUserLoggedIn(): Flow<Boolean>
    suspend fun login(email: String, password: String): Result<Unit>
    suspend fun signUp(email: String, password: String, username: String): Result<Unit>
    suspend fun logout(): Result<Unit>
}
