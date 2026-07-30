package com.ivanmacieldxz.truce.ui.auth

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val username: String = "",
    val isLoginMode: Boolean = true,
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null,
    val debugError: String? = null
)
