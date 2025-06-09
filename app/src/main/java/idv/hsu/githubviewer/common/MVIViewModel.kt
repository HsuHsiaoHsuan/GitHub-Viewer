package idv.hsu.githubviewer.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

abstract class MVIViewModel<UiIntent, UiState>(
    initialUi: UiState
) : ViewModel() {

    private val _state = MutableStateFlow(initialUi)
    val stateFlow: StateFlow<UiState> = _state

    private val _intentChannel = Channel<UiIntent>(Channel.UNLIMITED)
    private val intentFlow = _intentChannel.receiveAsFlow()

    init {
        viewModelScope.launch {
            intentFlow.collect { intent ->
                handleIntent(intent)
            }
        }
    }

    protected abstract suspend fun handleIntent(intent: UiIntent)

    fun sendIntent(event: UiIntent) {
        viewModelScope.launch {
            _intentChannel.send(event)
        }
    }

    suspend fun setUiState(uiState: UiState) {
        _state.emit(uiState)
    }
}