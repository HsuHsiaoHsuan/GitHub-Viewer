package idv.hsu.githubviewer.presentation.userlist

import androidx.lifecycle.viewModelScope
import androidx.paging.ItemSnapshotList
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
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class UserListViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase
) : MVIViewModel<UserListUiIntent, UserListUiState>(
    initialUi = UserListUiState.Idle
) {

    private val _loadUsersEvent = MutableSharedFlow<Pair<Int?, Int?>>()

    private var _lastKeyword: String = ""
    val lastKeyword: String
        get() = _lastKeyword

    private var _snapshotList: ItemSnapshotList<User>? = null
    val snapshotList: ItemSnapshotList<User>?
        get() = _snapshotList

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
            is UserListUiIntent.FetchData -> fetchUsers(intent.since, intent.perPage)
            is UserListUiIntent.SetSearchKeyword -> setKeyword(intent.keyword)
            is UserListUiIntent.SetSnapShotList -> setSnapshotList(intent.snapshotList)
        }
    }

    private fun fetchUsers(since: Int?, perPage: Int?) {
        Timber.d("fetchUsers, since: $since, perPage: $perPage")
        viewModelScope.launch {
            _loadUsersEvent.emit(Pair(since, perPage))
        }
    }

    private fun setKeyword(keyword: String) {
        Timber.d("setKeyword: $keyword")
        _lastKeyword = keyword
    }

    private fun setSnapshotList(snapshotList: ItemSnapshotList<User>?) {
        Timber.d("setSnapshotList, size: ${snapshotList?.size}")
        _snapshotList = snapshotList
    }
}

sealed class UserListUiIntent {
    data class FetchData(val since: Int? = null, val perPage: Int? = null) : UserListUiIntent()
    data class SetSearchKeyword(val keyword: String) : UserListUiIntent()
    data class SetSnapShotList(val snapshotList: ItemSnapshotList<User>?) : UserListUiIntent()
}

sealed class UserListUiState {
    data object Idle : UserListUiState()
    data object Loading : UserListUiState()
    data class Success(val users: List<User>) : UserListUiState()
    data class Error(val code: Int?, val message: String?) : UserListUiState()
}