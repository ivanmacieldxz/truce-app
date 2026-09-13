package com.ivanmacieldxz.truce.services

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.ivanmacieldxz.truce.domain.repository.AuthRepository
import com.ivanmacieldxz.truce.domain.repository.UserRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TruceFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var userRepository: UserRepository

    @Inject
    lateinit var authRepository: AuthRepository

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        serviceScope.launch {
            val isLoggedIn = authRepository.isUserLoggedIn().firstOrNull() ?: false
            if (isLoggedIn) {
                userRepository.updateFcmToken(token)
            }
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val notificationTitle = remoteMessage.notification?.title
            ?: remoteMessage.data["title"]
            ?: "Truce"
        val notificationBody = remoteMessage.notification?.body
            ?: remoteMessage.data["body"]
            ?: ""

        val type = remoteMessage.data["type"]
        val (channelId, navTab) = when (type) {
            "FRIENDSHIP_REQUEST", "FRIEND_REQUEST", "FRIENDSHIP_ACCEPTED" -> {
                NotificationHelper.CHANNEL_ID_FRIENDSHIPS to "inbox"
            }
            "TIME_REQUEST", "TIME_GRANT" -> {
                NotificationHelper.CHANNEL_ID_TIME_REQUESTS to "inbox"
            }
            else -> {
                NotificationHelper.CHANNEL_ID_GENERAL to null
            }
        }

        NotificationHelper.showNotification(
            context = applicationContext,
            title = notificationTitle,
            body = notificationBody,
            channelId = channelId,
            navTab = navTab
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
}
