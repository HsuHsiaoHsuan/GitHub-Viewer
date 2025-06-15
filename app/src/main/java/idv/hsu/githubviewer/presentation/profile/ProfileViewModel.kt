package idv.hsu.githubviewer.presentation.profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import dagger.hilt.android.lifecycle.HiltViewModel
import idv.hsu.githubviewer.common.MVIViewModel
import idv.hsu.githubviewer.core.DEFAULT_PER_PAGE
import idv.hsu.githubviewer.core.DEFAULT_START_PAGE_INDEX
import idv.hsu.githubviewer.domain.common.DomainResult
import idv.hsu.githubviewer.domain.model.GetRepositoryRequestParams
import idv.hsu.githubviewer.domain.model.Repository
import idv.hsu.githubviewer.domain.model.User
import idv.hsu.githubviewer.domain.usecase.GetRepositoryUseCase
import idv.hsu.githubviewer.domain.usecase.GetUserByIdUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getUserByIdUseCase: GetUserByIdUseCase,
    private val getRepositoryUseCase: GetRepositoryUseCase,
    savedStateHandle: SavedStateHandle
) : MVIViewModel<ProfileUiIntent, ProfileListUiState>(
    initialUi = ProfileListUiState.Idle
) {
    private val currentUser: User = savedStateHandle.get<User>("user")
        ?: throw IllegalStateException("User argument is missing in SavedStateHandle")

    private var fetchUserJob: Job? = null

    private val _loadRepositoriesEvent = MutableSharedFlow<GetRepositoryRequestParams>()

    @OptIn(ExperimentalCoroutinesApi::class)
    val repositories: Flow<PagingData<Repository>> = _loadRepositoriesEvent
        .flatMapLatest { request ->
            getRepositoryUseCase(
                GetRepositoryRequestParams(
                    username = request.username,
                    type = request.type,
                    sort = request.sort,
                    direction = request.direction,
                    perPage = request.perPage,
                    page = request.page
                )
            )
        }
        .cachedIn(viewModelScope)

    override suspend fun handleIntent(intent: ProfileUiIntent) {
        when (intent) {
            is ProfileUiIntent.FetchUserById -> {
                fetchUserById(currentUser.id.toString())
            }

            is ProfileUiIntent.FetchRepository -> {
                fetchRepositories(
                    GetRepositoryRequestParams(
                        username = currentUser.login,
                        type = intent.type,
                        sort = intent.sort,
                        direction = intent.direction,
                        perPage = intent.perPage ?: DEFAULT_PER_PAGE,
                        page = DEFAULT_START_PAGE_INDEX
                    )
                )
            }
        }
    }

    private fun fetchUserById(id: String) {
        fetchUserJob?.cancel()
        fetchUserJob = viewModelScope.launch {
            getUserByIdUseCase(id).collect { result ->
                when (result) {
                    is DomainResult.Success -> {
                        setUiState(ProfileListUiState.SuccessUser(result.data))
                    }

                    is DomainResult.Error -> {
                        setUiState(ProfileListUiState.ErrorUser(result.code, result.error))
                    }
                }
            }
        }
    }

    private fun fetchRepositories(params: GetRepositoryRequestParams) {
        viewModelScope.launch {
            _loadRepositoriesEvent.emit(params)
        }
    }
}

sealed class ProfileUiIntent {
    data object FetchUserById : ProfileUiIntent()
    data class FetchRepository(
        val type: String? = null, val sort: String? = null,
        val direction: String? = null, val perPage: Int? = null
    ) : ProfileUiIntent()
}

sealed class ProfileListUiState {
    data object Idle : ProfileListUiState()
    data class SuccessUser(val user: User) : ProfileListUiState()
    data class ErrorUser(val code: Int?, val message: String?) : ProfileListUiState()
}