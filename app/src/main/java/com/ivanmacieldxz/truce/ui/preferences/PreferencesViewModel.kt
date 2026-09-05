package com.ivanmacieldxz.truce.ui.preferences

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivanmacieldxz.truce.data.remote.UserDto
import com.ivanmacieldxz.truce.domain.repository.AuthRepository
import com.ivanmacieldxz.truce.domain.repository.UserRepository
import com.ivanmacieldxz.truce.domain.util.AuthValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PreferencesUiState(
    val isLoadingProfile: Boolean = false,
    val userProfile: UserDto? = null,
    val isUpdating: Boolean = false,
    val isDeleting: Boolean = false,
    val snackbarMessage: String? = null
)

@HiltViewModel
class PreferencesViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PreferencesUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingProfile = true) }
            userRepository.getMyProfile()
                .onSuccess { profile ->
                    _uiState.update { it.copy(isLoadingProfile = false, userProfile = profile) }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoadingProfile = false,
                            snackbarMessage = error.message ?: "Error al cargar el perfil"
                        )
                    }
                }
        }
    }

    fun updateUsername(newUsername: String, onFinished: (Boolean) -> Unit) {
        val trimmed = AuthValidator.formatUsername(newUsername.trim())
        if (!AuthValidator.isValidUsername(trimmed)) {
            _uiState.update {
                it.copy(snackbarMessage = "Nombre de usuario inválido (mínimo 5 caracteres, minúsculas, números, puntos o guiones bajos).")
            }
            onFinished(false)
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isUpdating = true) }
            userRepository.updateUsername(trimmed)
                .onSuccess { updatedProfile ->
                    _uiState.update {
                        it.copy(
                            isUpdating = false,
                            userProfile = updatedProfile,
                            snackbarMessage = "Nombre de usuario actualizado con éxito"
                        )
                    }
                    onFinished(true)
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isUpdating = false,
                            snackbarMessage = error.message ?: "Error al actualizar el nombre de usuario"
                        )
                    }
                    onFinished(false)
                }
        }
    }

    fun updateEmail(newEmail: String, onFinished: (Boolean) -> Unit) {
        val trimmed = newEmail.trim()
        if (!AuthValidator.isValidEmail(trimmed)) {
            _uiState.update {
                it.copy(snackbarMessage = "Por favor, ingresá un correo electrónico válido.")
            }
            onFinished(false)
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isUpdating = true) }
            userRepository.updateEmail(trimmed)
                .onSuccess { updatedProfile ->
                    _uiState.update {
                        it.copy(
                            isUpdating = false,
                            userProfile = updatedProfile,
                            snackbarMessage = "Correo electrónico actualizado con éxito"
                        )
                    }
                    onFinished(true)
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isUpdating = false,
                            snackbarMessage = error.message ?: "Error al actualizar el correo electrónico"
                        )
                    }
                    onFinished(false)
                }
        }
    }

    fun deleteAccount() {
        viewModelScope.launch {
            _uiState.update { it.copy(isDeleting = true) }
            userRepository.deleteAccount()
                .onSuccess {
                    _uiState.update { it.copy(isDeleting = false) }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isDeleting = false,
                            snackbarMessage = error.message ?: "Error al eliminar la cuenta"
                        )
                    }
                }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }

    fun clearSnackbarMessage() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }
}
