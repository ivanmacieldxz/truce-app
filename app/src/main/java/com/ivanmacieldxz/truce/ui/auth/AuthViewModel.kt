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
            is AuthEvent.EmailChanged -> {
                _uiState.update { it.copy(email = event.email, error = null) }
            }
            is AuthEvent.PasswordChanged -> {
                _uiState.update { it.copy(password = event.password, error = null) }
            }
            is AuthEvent.UsernameChanged -> {
                _uiState.update { it.copy(username = event.username, error = null) }
            }
            is AuthEvent.ToggleMode -> {
                _uiState.update {
                    it.copy(
                        isLoginMode = !it.isLoginMode,
                        error = null,
                        password = "",
                        email = "",
                        username = ""
                    )
                }
            }
            is AuthEvent.Submit -> submit()
        }
    }

    private fun submit() {
        val state = _uiState.value
        if (state.email.isBlank() || state.password.isBlank()) {
            _uiState.update { it.copy(error = "Completa los campos obligatorios") }
            return
        }
        if (!state.isLoginMode && state.username.isBlank()) {
            _uiState.update { it.copy(error = "El nombre de usuario es obligatorio") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            val result = if (state.isLoginMode) {
                authRepository.login(state.email, state.password)
            } else {
                authRepository.signUp(state.email, state.password, state.username)
            }

            result.onSuccess {
                _uiState.update { it.copy(isLoading = false) }
            }.onFailure { exception ->
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = exception.message ?: "Ocurrió un error inesperado"
                    )
                }
            }
        }
    }
}
