package com.ivanmacieldxz.truce.ui.friendships

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivanmacieldxz.truce.data.remote.FriendDto
import com.ivanmacieldxz.truce.data.remote.UserSummaryDto
import com.ivanmacieldxz.truce.domain.repository.FriendshipsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FriendshipsUiState(
    val isLoadingFriends: Boolean = false,
    val friends: List<FriendDto> = emptyList(),
    val error: String? = null,
    
    val searchQuery: String = "",
    val isSearching: Boolean = false,
    val searchResults: List<UserSummaryDto> = emptyList(),
    
    val snackbarMessage: String? = null
)

@HiltViewModel
class FriendshipsViewModel @Inject constructor(
    private val friendshipsRepository: FriendshipsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FriendshipsUiState())
    val uiState = _uiState.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadFriends()
    }

    fun loadFriends() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingFriends = true, error = null) }
            val result = friendshipsRepository.getFriends(page = 1, limit = 20)
            result.onSuccess { friends ->
                _uiState.update { it.copy(isLoadingFriends = false, friends = friends) }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoadingFriends = false, error = e.message) }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        searchJob?.cancel()
        
        if (query.isBlank()) {
            _uiState.update { it.copy(searchResults = emptyList(), isSearching = false) }
            return
        }

        searchJob = viewModelScope.launch {
            delay(500) // Debounce
            _uiState.update { it.copy(isSearching = true) }
            val result = friendshipsRepository.searchUsers(query, page = 1, limit = 20)
            result.onSuccess { users ->
                _uiState.update { it.copy(isSearching = false, searchResults = users) }
            }.onFailure {
                _uiState.update { it.copy(isSearching = false, searchResults = emptyList()) }
            }
        }
    }

    fun sendFriendRequest(targetUserId: String) {
        viewModelScope.launch {
            val result = friendshipsRepository.sendFriendRequest(targetUserId)
            result.onSuccess {
                _uiState.update { it.copy(snackbarMessage = "Solicitud enviada exitosamente") }
            }.onFailure { e ->
                _uiState.update { it.copy(snackbarMessage = "Error al enviar solicitud: ${e.message}") }
            }
        }
    }
    
    fun removeFriend(friendId: String) {
        viewModelScope.launch {
            val result = friendshipsRepository.removeFriend(friendId)
            result.onSuccess {
                _uiState.update { it.copy(snackbarMessage = "Amigo eliminado") }
                loadFriends()
            }.onFailure { e ->
                _uiState.update { it.copy(snackbarMessage = "Error al eliminar: ${e.message}") }
            }
        }
    }

    fun clearSnackbarMessage() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }
}
