package idv.hsu.githubviewer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import idv.hsu.githubviewer.domain.common.DomainResult
import idv.hsu.githubviewer.domain.usecase.GetRepositoryUseCase
import idv.hsu.githubviewer.domain.usecase.GetUserUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase,
    private val getRepositoryUseCase: GetRepositoryUseCase
) : ViewModel() {

    fun fetchUsers(since: Int, perPage: Int? = null) {
        viewModelScope.launch {
            getUserUseCase(since, perPage).collect { result ->
                when (result) {
                    is DomainResult.Success -> {
                        // Handle success
                    }

                    is DomainResult.Error -> {
                        // Handle error
                    }
                }
            }
        }
    }

    fun fetchRepositories(username: String) {
        viewModelScope.launch {
            getRepositoryUseCase(
                username = username,
                type = null,
                sort = null,
                direction = null,
                perPage = null
            ).collect { result ->
                when (result) {
                    is DomainResult.Success -> {
                        // Handle success
                    }

                    is DomainResult.Error -> {
                        // Handle error
                    }
                }
            }
        }
    }
}