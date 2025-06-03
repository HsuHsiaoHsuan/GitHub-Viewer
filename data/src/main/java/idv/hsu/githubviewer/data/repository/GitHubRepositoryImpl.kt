package idv.hsu.githubviewer.data.repository

import android.util.Log
import idv.hsu.githubviewer.data.ApiResultWrapper
import idv.hsu.githubviewer.data.dto.toDomain
import idv.hsu.githubviewer.data.source.GitHubRemoteDataSource
import idv.hsu.githubviewer.domain.common.DomainResult
import idv.hsu.githubviewer.domain.model.Repository
import idv.hsu.githubviewer.domain.model.User
import idv.hsu.githubviewer.domain.repository.GitHubRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val NETWORK_ERROR_MESSAGE = "Network error occurred"

class GitHubRepositoryImpl @Inject constructor(
    private val remoteDataSource: GitHubRemoteDataSource
) : GitHubRepository {

    override fun getUsers(since: Int, perPage: Int?): Flow<DomainResult<List<User>>> {
        return remoteDataSource.getUsers(since, perPage).map { apiResultWrapper ->
            when (apiResultWrapper) {
                is ApiResultWrapper.Success -> {
                    Log.e("FREEMAN", "repo, Success")
                    DomainResult.Success(apiResultWrapper.data.map { it.toDomain() })
                }

                is ApiResultWrapper.Error -> {
                    Log.e(
                        "FREEMAN",
                        "repo, Error, code: ${apiResultWrapper.code}, error: ${apiResultWrapper.error}"
                    )
                    DomainResult.Error(apiResultWrapper.code, apiResultWrapper.error)
                }

                is ApiResultWrapper.NetworkError -> {
                    Log.e("FREEMAN", "repo, NetworkError")
                    DomainResult.Error(null, NETWORK_ERROR_MESSAGE)
                }
            }
        }.catch { throwable ->
            Log.e("FREEMAN", "repo, exception")
            emit(DomainResult.Error(null, throwable.message))
        }
    }

    override fun getUserById(id: String): Flow<DomainResult<User>> {
        return remoteDataSource.getUserById(id)
            .map { apiResultWrapper ->
                when (apiResultWrapper) {
                    is ApiResultWrapper.Success -> {
                        DomainResult.Success(apiResultWrapper.data.toDomain())
                    }

                    is ApiResultWrapper.Error -> {
                        DomainResult.Error(apiResultWrapper.code, apiResultWrapper.error)
                    }

                    is ApiResultWrapper.NetworkError ->
                        DomainResult.Error(null, NETWORK_ERROR_MESSAGE)
                }
            }.catch { throwable ->
                emit(DomainResult.Error(null, throwable.message))
            }
    }

    override fun getRepositories(
        username: String,
        type: String?,
        sort: String?,
        direction: String?,
        perPage: Int?
    ): Flow<DomainResult<List<Repository>>> {
        return remoteDataSource.getRepositories(username, type, sort, direction, perPage)
            .map { apiResultWrapper ->
                when (apiResultWrapper) {
                    is ApiResultWrapper.Success -> {
                        DomainResult.Success(apiResultWrapper.data.map { it.toDomain() })
                    }

                    is ApiResultWrapper.Error -> {
                        DomainResult.Error(apiResultWrapper.code, apiResultWrapper.error)
                    }

                    is ApiResultWrapper.NetworkError ->
                        DomainResult.Error(null, NETWORK_ERROR_MESSAGE)
                }
            }.catch { throwable ->
                emit(DomainResult.Error(null, throwable.message))
            }
    }
}