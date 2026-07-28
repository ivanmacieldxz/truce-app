package com.ivanmacieldxz.truce.ui.auth

sealed class AuthEvent {
    data class EmailChanged(val email: String) : AuthEvent()
    data class PasswordChanged(val password: String) : AuthEvent()
    data class UsernameChanged(val username: String) : AuthEvent()
    data object ToggleMode : AuthEvent()
    data object Submit : AuthEvent()
}
