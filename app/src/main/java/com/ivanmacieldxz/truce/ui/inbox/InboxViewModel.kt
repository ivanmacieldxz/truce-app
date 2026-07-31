package com.ivanmacieldxz.truce.ui.inbox

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivanmacieldxz.truce.data.remote.FriendshipRequestDto
import com.ivanmacieldxz.truce.domain.repository.FriendshipsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class InboxUiState(
    val isLoading: Boolean = false,
    val incomingRequests: List<FriendshipRequestDto> = emptyList(),
    val error: String? = null,
    val snackbarMessage: String? = null
)

@HiltViewModel
class InboxViewModel @Inject constructor(
    private val friendshipsRepository: FriendshipsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(InboxUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadIncomingRequests()
    }

    fun loadIncomingRequests() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = friendshipsRepository.getFriendRequests(type = "incoming", page = 1, limit = 20)
            result.onSuccess { requests ->
                _uiState.update { it.copy(isLoading = false, incomingRequests = requests) }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun respondToRequest(requestId: String, accept: Boolean) {
        viewModelScope.launch {
            val result = friendshipsRepository.respondToFriendRequest(requestId, accept)
            result.onSuccess {
                val msg = if (accept) "Solicitud aceptada" else "Solicitud rechazada"
                _uiState.update { it.copy(snackbarMessage = msg) }
                loadIncomingRequests()
            }.onFailure { e ->
                _uiState.update { it.copy(snackbarMessage = "Error: ${e.message}") }
            }
        }
    }

    fun clearSnackbarMessage() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }
}
