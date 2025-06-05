package idv.hsu.githubviewer.data.source

import idv.hsu.githubviewer.data.ApiResultWrapper
import idv.hsu.githubviewer.data.dto.RepositoryDto
import idv.hsu.githubviewer.data.dto.UserDto
import idv.hsu.githubviewer.data.source.remote.GitHubRepositoryPagingSource
import idv.hsu.githubviewer.data.source.remote.GitHubUserPagingSource
import kotlinx.coroutines.flow.Flow

interface GitHubRemoteDataSource {

    @Deprecated("Use 'getUsersPagingSource' to get paged data")
    fun getUsers(since: Int, perPage: Int?): Flow<ApiResultWrapper<List<UserDto>>>

    fun getUsersPagingSource(since: Int): GitHubUserPagingSource

    fun getUserById(username: String): Flow<ApiResultWrapper<UserDto>>

    fun getRepositories(
        username: String,
        type: String?,
        sort: String?,
        direction: String?,
        perPage: Int?,
        page: Int?
    ): Flow<ApiResultWrapper<List<RepositoryDto>>>

    fun getRepositoriesPagingSource(
        username: String,
        type: String?,
        sort: String?,
        direction: String?,
        perPage: Int,
        page: Int
    ): GitHubRepositoryPagingSource
}