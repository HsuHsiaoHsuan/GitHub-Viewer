package idv.hsu.githubviewer.presentation.userlist

import android.util.Log
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import idv.hsu.githubviewer.common.MVIViewModel
import idv.hsu.githubviewer.core.DEFAULT_PER_PAGE
import idv.hsu.githubviewer.core.DEFAULT_START_USER_ID
import idv.hsu.githubviewer.domain.model.User
import idv.hsu.githubviewer.domain.usecase.GetUserUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserListViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase
) : MVIViewModel<UserListUiIntent, UserListUiState>(
    initialUi = UserListUiState.Idle
) {
    private val _loadUsersEvent = MutableSharedFlow<Pair<Int?, Int?>>(replay = 1)
    @OptIn(ExperimentalCoroutinesApi::class)
    val users: Flow<PagingData<User>> = _loadUsersEvent
        .flatMapLatest {
            getUserUseCase(
                since = it.first ?: DEFAULT_START_USER_ID,
                perPage = it.second ?: DEFAULT_PER_PAGE
            )

        }
        .cachedIn(viewModelScope)

    override suspend fun handleIntent(intent: UserListUiIntent) {
        when (intent) {
            is UserListUiIntent.FetchData -> {
                Log.e("UserListViewModel", "handleIntent: FetchData")
                fetchUsers(intent.since, intent.perPage)
            }
        }
    }

    private fun fetchUsers(since: Int?, perPage: Int?) {
        Log.e("UserListViewModel", "fetchUsers: since: $since, perPage: $perPage")
        viewModelScope.launch {
            _loadUsersEvent.emit(Pair(since, perPage))
        }
    }
}

sealed class UserListUiIntent {
    data class FetchData(val since: Int? = null, val perPage: Int? = null) : UserListUiIntent()
}

sealed class UserListUiState {
    data object Idle : UserListUiState()
    data object Loading : UserListUiState()
    data class Success(val users: List<User>) : UserListUiState()
    data class Error(val code: Int?, val message: String?) : UserListUiState()
}