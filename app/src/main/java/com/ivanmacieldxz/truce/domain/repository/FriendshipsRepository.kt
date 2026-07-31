package com.ivanmacieldxz.truce.domain.repository

import com.ivanmacieldxz.truce.data.remote.FriendDto
import com.ivanmacieldxz.truce.data.remote.FriendshipRequestDto
import com.ivanmacieldxz.truce.data.remote.UserSummaryDto

interface FriendshipsRepository {
    suspend fun searchUsers(query: String, page: Int = 1, limit: Int = 20): Result<List<UserSummaryDto>>
    
    suspend fun getFriends(page: Int = 1, limit: Int = 20): Result<List<FriendDto>>
    
    suspend fun getFriendRequests(type: String? = null, page: Int = 1, limit: Int = 20): Result<List<FriendshipRequestDto>>
    
    suspend fun sendFriendRequest(targetUserId: String): Result<Unit>
    
    suspend fun respondToFriendRequest(requestId: String, accept: Boolean): Result<Unit>
    
    suspend fun removeFriend(friendId: String): Result<Unit>
}
