package com.ivanmacieldxz.truce

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.messaging.FirebaseMessaging
import com.ivanmacieldxz.truce.domain.repository.AuthRepository
import com.ivanmacieldxz.truce.domain.repository.UserRepository
import com.ivanmacieldxz.truce.ui.main.MainTab
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    val isUserLoggedIn: StateFlow<Boolean?> = authRepository.isUserLoggedIn()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    private val _targetTab = MutableStateFlow<MainTab?>(null)
    val targetTab: StateFlow<MainTab?> = _targetTab.asStateFlow()

    init {
        viewModelScope.launch {
            authRepository.isUserLoggedIn().collect { loggedIn ->
                android.util.Log.d("FCM", "isUserLoggedIn auth state changed: $loggedIn")
                if (loggedIn == true) {
                    syncFcmToken()
                }
            }
        }
    }

    fun syncFcmToken() {
        android.util.Log.d("FCM", "Requesting FCM registration token from Firebase...")
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                android.util.Log.i("FCM", "Firebase registration token retrieved: $token")
                if (!token.isNullOrBlank()) {
                    viewModelScope.launch {
                        android.util.Log.d("FCM", "Sending FCM token to backend via PATCH /users/me/fcm-token...")
                        val result = userRepository.updateFcmToken(token)
                        result.onSuccess { user ->
                            android.util.Log.i("FCM", "FCM token successfully registered on backend for user: @${user.username} (fcmToken=${user.fcmToken?.take(15)}...)")
                        }.onFailure { error ->
                            android.util.Log.e("FCM", "Failed to register FCM token on backend: ${error.message}", error)
                        }
                    }
                }
            } else {
                android.util.Log.w("FCM", "Failed to obtain FCM token from Firebase", task.exception)
            }
        }
    }

    fun setTargetTab(tab: MainTab) {
        _targetTab.value = tab
    }

    fun clearTargetTab() {
        _targetTab.value = null
    }
}
