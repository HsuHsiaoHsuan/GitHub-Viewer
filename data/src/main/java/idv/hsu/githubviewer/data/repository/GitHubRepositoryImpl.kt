package idv.hsu.githubviewer.data.repository

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

class GitHubRepositoryImpl @Inject constructor(
    private val remoteDataSource: GitHubRemoteDataSource
) : GitHubRepository {
    override fun getUsers(since: Int, perPage: Int?): Flow<DomainResult<List<User>>> {
        return remoteDataSource.getUser(since, perPage).map { apiResultWrapper ->
            when (apiResultWrapper) {
                is ApiResultWrapper.Success -> {
                    DomainResult.Success(apiResultWrapper.data.map { it.toDomain() })
                }

                is ApiResultWrapper.Error -> {
                    DomainResult.Error(apiResultWrapper.code, apiResultWrapper.error)
                }

                is ApiResultWrapper.NetworkError ->
                    DomainResult.Error(null, "Network error occurred")
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
                        DomainResult.Error(null, "Network error occurred")
                }
            }.catch { throwable ->
                emit(DomainResult.Error(null, throwable.message))
            }
    }
}