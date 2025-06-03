package idv.hsu.githubviewer.ui.userlist

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import idv.hsu.githubviewer.common.MVIViewModel
import idv.hsu.githubviewer.domain.common.DomainResult
import idv.hsu.githubviewer.domain.model.User
import idv.hsu.githubviewer.domain.usecase.GetUserUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserListViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase
) : MVIViewModel<UserListUiIntent, UserListUiState>(
    initialUi = UserListUiState.Idle
) {
    private var fetchJob: Job? = null

    init {
        sendIntent(UserListUiIntent.FetchData(0, 100))
    }

    override suspend fun handleIntent(intent: UserListUiIntent) {
        when (intent) {
            is UserListUiIntent.FetchData -> fetchUsers(
                since = intent.since,
                perPage = intent.perPage
            )
        }
    }

    private fun fetchUsers(since: Int, perPage: Int? = null) {
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            setUiState(UserListUiState.Loading)
            getUserUseCase(since, perPage).collect { result ->
                when (result) {
                    is DomainResult.Success -> {
                        setUiState(UserListUiState.Success(result.data))
                    }

                    is DomainResult.Error -> {
                        setUiState(UserListUiState.Error(result.code, result.error))
                    }
                }
            }
        }
    }
}

sealed class UserListUiIntent {
    data class FetchData(val since: Int, val perPage: Int?) : UserListUiIntent()
}

sealed class UserListUiState {
    data object Idle : UserListUiState()
    data object Loading : UserListUiState()
    data class Success(val users: List<User>) : UserListUiState()
    data class Error(val code: Int?, val message: String?) : UserListUiState()
}