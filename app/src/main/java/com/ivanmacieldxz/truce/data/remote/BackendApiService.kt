package com.ivanmacieldxz.truce.data.remote

import retrofit2.http.GET

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

data class UserDto(
    val id: String,
    val email: String,
    val username: String,
    val fcmToken: String?,
    val createdAt: String,
    val updatedAt: String
)

data class UserSummaryDto(
    val id: String,
    val username: String
)

data class FriendDto(
    val id: String,
    val friendId: String,
    val username: String,
    val createdAt: String
)

data class FriendshipRequestDto(
    val id: String,
    val userId: String,
    val username: String,
    val type: String, // INCOMING or OUTGOING
    val createdAt: String
)

data class CreateFriendshipDto(
    val targetUserId: String
)

data class UpdateFriendshipDto(
    val status: String // ACCEPTED or REJECTED
)

interface BackendApiService {
    
    @GET("users/me")
    suspend fun getMyProfile(): UserDto

    @GET("users")
    suspend fun searchUsers(
        @Query("q") query: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): List<UserSummaryDto>

    @GET("friends")
    suspend fun getFriends(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): List<FriendDto>

    @GET("friends/requests")
    suspend fun getFriendRequests(
        @Query("type") type: String? = null,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): List<FriendshipRequestDto>

    @POST("friends/requests")
    suspend fun sendFriendRequest(@Body body: CreateFriendshipDto)

    @PATCH("friends/requests/{id}")
    suspend fun respondToFriendRequest(
        @Path("id") id: String,
        @Body body: UpdateFriendshipDto
    )

    @DELETE("friends/{friendId}")
    suspend fun removeFriend(@Path("friendId") friendId: String)
}
