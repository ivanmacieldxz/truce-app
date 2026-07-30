package com.ivanmacieldxz.truce.ui.auth

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val username: String = "",
    val fullName: String = "",
    val passwordVisible: Boolean = false,
    val isLoginMode: Boolean = true,
    val signupStep: SignupStep = SignupStep.NAME_EMAIL,
    
    // Password validation flags
    val hasPasswordMinLength: Boolean = false,
    val hasPasswordUppercase: Boolean = false,
    val hasPasswordLowercase: Boolean = false,
    val hasPasswordDigit: Boolean = false,

    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null,
    val debugError: String? = null
)

enum class SignupStep {
    NAME_EMAIL,
    USERNAME,
    PASSWORD,
    CONFIRMATION
}
