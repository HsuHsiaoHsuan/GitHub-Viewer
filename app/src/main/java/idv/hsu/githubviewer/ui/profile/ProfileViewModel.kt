package idv.hsu.githubviewer.ui.profile

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import idv.hsu.githubviewer.common.MVIViewModel
import idv.hsu.githubviewer.domain.common.DomainResult
import idv.hsu.githubviewer.domain.model.Repository
import idv.hsu.githubviewer.domain.model.User
import idv.hsu.githubviewer.domain.usecase.GetRepositoryUseCase
import idv.hsu.githubviewer.domain.usecase.GetUserByIdUseCase
import kotlinx.coroutines.Job
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
    private var fetchRepositoryJob: Job? = null

    init {
        sendIntent(ProfileUiIntent.FetchUserById(currentUser.id.toString()))
        sendIntent(
            ProfileUiIntent.FetchRepository(
                username = currentUser.login,
                type = null,
                sort = null,
                direction = null,
                perPage = 100
            )
        )
    }

    override suspend fun handleIntent(intent: ProfileUiIntent) {
        when (intent) {
            is ProfileUiIntent.FetchUserById -> {
                fetchUserById(intent.id)
            }

            is ProfileUiIntent.FetchRepository -> {
                fetchRepositories(
                    username = intent.username,
                    type = intent.type,
                    sort = intent.sort,
                    direction = intent.direction,
                    perPage = intent.perPage
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

    private fun fetchRepositories(
        username: String, type: String? = null, sort: String? = null,
        direction: String? = null, perPage: Int? = null
    ) {
        fetchRepositoryJob?.cancel()
        fetchRepositoryJob = viewModelScope.launch {
            setUiState(ProfileListUiState.LoadingRepository)
            getRepositoryUseCase(username, type, sort, direction, perPage).collect { result ->
                when (result) {
                    is DomainResult.Success -> {
                        val filteredData = result.data.filter { it.fork == false }
                        setUiState(ProfileListUiState.SuccessRepository(filteredData))
                    }

                    is DomainResult.Error -> {
                        Log.e("FREEMAN", "Error fetching repositories: ${result.error}")
                        setUiState(ProfileListUiState.ErrorRepository(result.code, result.error))
                    }
                }
            }
        }
    }
}

sealed class ProfileUiIntent {
    data class FetchUserById(val id: String) : ProfileUiIntent()
    data class FetchRepository(
        val username: String, val type: String? = null, val sort: String? = null,
        val direction: String? = null, val perPage: Int? = null
    ) : ProfileUiIntent()
}

sealed class ProfileListUiState {
    data object Idle : ProfileListUiState()
    data object LoadingRepository : ProfileListUiState()
    data class SuccessUser(val user: User) : ProfileListUiState()
    data class SuccessRepository(val repositories: List<Repository>) : ProfileListUiState()
    data class ErrorUser(val code: Int?, val message: String?) : ProfileListUiState()
    data class ErrorRepository(val code: Int?, val message: String?) : ProfileListUiState()
}