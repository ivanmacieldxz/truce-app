package com.ivanmacieldxz.truce.data.remote

import retrofit2.http.GET

data class UserDto(
    val id: String,
    val email: String,
    val username: String,
    val fcmToken: String?,
    val createdAt: String,
    val updatedAt: String
)

interface BackendApiService {
    
    @GET("users/me")
    suspend fun getMyProfile(): UserDto
}
