package com.ivanmacieldxz.truce.domain.repository

import com.ivanmacieldxz.truce.data.remote.UserDto

interface UserRepository {
    suspend fun getMyProfile(): Result<UserDto>
    suspend fun updateUsername(username: String): Result<UserDto>
    suspend fun updateEmail(email: String): Result<UserDto>
    suspend fun deleteAccount(): Result<Unit>
}
