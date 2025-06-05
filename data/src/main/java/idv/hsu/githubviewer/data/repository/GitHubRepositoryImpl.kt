package idv.hsu.githubviewer.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import idv.hsu.githubviewer.data.ApiResultWrapper
import idv.hsu.githubviewer.data.dto.RepositoryDto
import idv.hsu.githubviewer.data.dto.UserDto
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

    @Deprecated("Use 'getUsersStream' to get paged data")
    override fun getUsers(since: Int, perPage: Int?): Flow<DomainResult<List<User>>> {
        return remoteDataSource.getUsers(since, perPage).map { apiResultWrapper ->
            when (apiResultWrapper) {
                is ApiResultWrapper.Success -> {
                    DomainResult.Success(apiResultWrapper.data.map { it.toDomain() })
                }

                is ApiResultWrapper.Error -> {
                    DomainResult.Error(apiResultWrapper.code, apiResultWrapper.error)
                }

                is ApiResultWrapper.NetworkError -> {
                    DomainResult.Error(null, NETWORK_ERROR_MESSAGE)
                }
            }
        }.catch { throwable ->
            emit(DomainResult.Error(null, throwable.message))
        }
    }

    override fun getUsersStream(since: Int, perPage: Int): Flow<PagingData<User>> {
        return Pager(
            config = PagingConfig(
                pageSize = perPage,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { remoteDataSource.getUsersPagingSource(since) }
        )
            .flow
            .map { pagingData: PagingData<UserDto> ->
                pagingData.map { userDto ->
                    userDto.toDomain()
                }
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

    @Deprecated("Use 'getRepositoriesStream' to get paged data")
    override fun getRepositories(
        username: String,
        type: String?,
        sort: String?,
        direction: String?,
        perPage: Int?
    ): Flow<DomainResult<List<Repository>>> {
        return remoteDataSource.getRepositories(username, type, sort, direction, perPage, perPage)
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

    override fun getRepositoriesStream(
        username: String,
        type: String?,
        sort: String?,
        direction: String?,
        perPage: Int,
        page: Int
    ): Flow<PagingData<Repository>> {
        return Pager(
            config = PagingConfig(
                pageSize = perPage,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                remoteDataSource.getRepositoriesPagingSource(
                    username,
                    type,
                    sort,
                    direction,
                    perPage,
                    page
                )
            }
        )
            .flow
            .map { pagingData: PagingData<RepositoryDto> ->
                pagingData.map { repositoryDto ->
                    repositoryDto.toDomain()
                }
            }
    }
}