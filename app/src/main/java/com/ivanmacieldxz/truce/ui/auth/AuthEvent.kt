package com.ivanmacieldxz.truce.ui.auth

sealed class AuthEvent {
    data class FullNameChanged(val fullName: String) : AuthEvent()
    data class EmailChanged(val email: String) : AuthEvent()
    data class PasswordChanged(val password: String) : AuthEvent()
    data class UsernameChanged(val username: String) : AuthEvent()
    data object ToggleMode : AuthEvent()
    data object NextStep : AuthEvent()
    data object PreviousStep : AuthEvent()
    data object TogglePasswordVisibility : AuthEvent()
    data object ManualLoginCheck : AuthEvent()
    data object Submit : AuthEvent()
}
