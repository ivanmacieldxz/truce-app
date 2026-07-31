package com.ivanmacieldxz.truce.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivanmacieldxz.truce.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

import com.ivanmacieldxz.truce.domain.util.AuthValidator

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    val isUserLoggedIn: StateFlow<Boolean> = authRepository.isUserLoggedIn()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()

    fun onEvent(event: AuthEvent) {
        when (event) {
            is AuthEvent.FullNameChanged -> {
                _uiState.update { it.copy(fullName = event.fullName, error = null, successMessage = null, debugError = null) }
            }
            is AuthEvent.EmailChanged -> {
                _uiState.update { it.copy(email = event.email, error = null, successMessage = null, debugError = null) }
            }
            is AuthEvent.PasswordChanged -> {
                val p = event.password
                _uiState.update { 
                    it.copy(
                        password = p, 
                        error = null, 
                        successMessage = null, 
                        debugError = null,
                        hasPasswordMinLength = AuthValidator.hasPasswordMinLength(p),
                        hasPasswordUppercase = AuthValidator.hasPasswordUppercase(p),
                        hasPasswordLowercase = AuthValidator.hasPasswordLowercase(p),
                        hasPasswordDigit = AuthValidator.hasPasswordDigit(p)
                    ) 
                }
            }
            is AuthEvent.UsernameChanged -> {
                val formattedUsername = AuthValidator.formatUsername(event.username)
                _uiState.update { it.copy(username = formattedUsername, error = null, successMessage = null, debugError = null) }
            }
            is AuthEvent.ToggleMode -> {
                _uiState.update {
                    it.copy(
                        isLoginMode = !it.isLoginMode,
                        signupStep = SignupStep.NAME_EMAIL,
                        error = null,
                        successMessage = null,
                        debugError = null,
                        password = "",
                        email = "",
                        username = "",
                        fullName = "",
                        passwordVisible = false,
                        hasPasswordMinLength = false,
                        hasPasswordUppercase = false,
                        hasPasswordLowercase = false,
                        hasPasswordDigit = false
                    )
                }
            }
            is AuthEvent.TogglePasswordVisibility -> {
                _uiState.update { it.copy(passwordVisible = !it.passwordVisible) }
            }
            is AuthEvent.NextStep -> nextStep()
            is AuthEvent.PreviousStep -> previousStep()
            is AuthEvent.ManualLoginCheck -> manualLoginCheck()
            is AuthEvent.Submit -> submit()
        }
    }

    private fun nextStep() {
        val state = _uiState.value
        when (state.signupStep) {
            SignupStep.NAME_EMAIL -> {
                if (!AuthValidator.isValidFullName(state.fullName)) {
                    _uiState.update { it.copy(error = "El nombre completo debe tener al menos 2 caracteres.") }
                    return
                }
                if (!AuthValidator.isValidEmail(state.email)) {
                    _uiState.update { it.copy(error = "Por favor, ingresá un correo electrónico válido.") }
                    return
                }
                // TODO: Verify email uniqueness in future
                _uiState.update { it.copy(signupStep = SignupStep.USERNAME, error = null) }
            }
            SignupStep.USERNAME -> {
                if (!AuthValidator.isValidUsername(state.username)) {
                    _uiState.update { it.copy(error = "El nombre de usuario debe tener al menos 5 caracteres y usar solo minúsculas, números, puntos o guiones bajos.") }
                    return
                }
                // TODO: Verify username uniqueness in future
                _uiState.update { it.copy(signupStep = SignupStep.PASSWORD, error = null) }
            }
            else -> {}
        }
    }

    private fun previousStep() {
        val state = _uiState.value
        when (state.signupStep) {
            SignupStep.PASSWORD -> _uiState.update { it.copy(signupStep = SignupStep.USERNAME, error = null) }
            SignupStep.USERNAME -> _uiState.update { it.copy(signupStep = SignupStep.NAME_EMAIL, error = null) }
            else -> {}
        }
    }

    private fun manualLoginCheck() {
        val state = _uiState.value
        if (state.email.isBlank() || state.password.isBlank()) return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = authRepository.login(state.email, state.password)
            result.onSuccess {
                _uiState.update { it.copy(isLoading = false) }
                // isUserLoggedIn will automatically emit true
            }.onFailure { exception ->
                if (exception is com.ivanmacieldxz.truce.domain.repository.AuthException) {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = "Aún no pudimos validar tu correo. Revisa tu bandeja de entrada o spam. (${exception.userMessage})"
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "Error al verificar la sesión. Intentá de nuevo.") }
                }
            }
        }
    }

    private fun submit() {
        val state = _uiState.value
        
        if (state.isLoginMode) {
            if (state.email.isBlank() || state.password.isBlank()) {
                _uiState.update { it.copy(error = "Completa los campos obligatorios", successMessage = null, debugError = null) }
                return
            }
        } else {
            if (!AuthValidator.isValidPassword(state.password)) {
                _uiState.update { it.copy(error = "La contraseña no cumple con los requisitos mínimos de seguridad.", successMessage = null, debugError = null) }
                return
            }
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, successMessage = null, debugError = null) }
            
            val result = if (state.isLoginMode) {
                authRepository.login(state.email, state.password)
            } else {
                authRepository.signUp(state.email, state.password, state.username, state.fullName)
            }

            result.onSuccess { message ->
                if (!state.isLoginMode) {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            signupStep = SignupStep.CONFIRMATION,
                            error = null,
                            debugError = null
                        )
                    }
                } else {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            successMessage = message,
                            error = null,
                            debugError = null
                        )
                    }
                }
            }.onFailure { exception ->
                if (exception is com.ivanmacieldxz.truce.domain.repository.AuthException) {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = exception.userMessage,
                            debugError = exception.debugMessage
                        )
                    }
                } else {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = exception.message ?: "Ocurrió un error inesperado",
                            debugError = exception.stackTraceToString()
                        )
                    }
                }
            }
        }
    }
}
