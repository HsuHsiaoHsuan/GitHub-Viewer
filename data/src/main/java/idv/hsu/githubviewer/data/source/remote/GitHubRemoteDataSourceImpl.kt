package idv.hsu.githubviewer.data.source.remote

import idv.hsu.githubviewer.data.ApiResultWrapper
import idv.hsu.githubviewer.data.dto.RepositoryDto
import idv.hsu.githubviewer.data.dto.UserDto
import idv.hsu.githubviewer.data.safeApiCall
import idv.hsu.githubviewer.data.source.GitHubRemoteDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GitHubRemoteDataSourceImpl @Inject constructor(
    private val api: GitHubApiService
) : GitHubRemoteDataSource {

    @Deprecated("Use 'getUsersPagingSource' to get paged data")
    override fun getUsers(since: Int, perPage: Int?): Flow<ApiResultWrapper<List<UserDto>>> =
        safeApiCall {
            api.getUsers(since, perPage)
        }

    override fun getUsersPagingSource(since: Int): GitHubUserPagingSource {
        return GitHubUserPagingSource(api, since)
    }

    override fun getUserById(id: String): Flow<ApiResultWrapper<UserDto>> = safeApiCall {
        api.getUserById(id)
    }

    override fun getRepositories(
        username: String,
        type: String?,
        sort: String?,
        direction: String?,
        perPage: Int?,
        page: Int?
    ): Flow<ApiResultWrapper<List<RepositoryDto>>> = safeApiCall {
        api.getUserRepositories(username, type, sort, direction, perPage, page)
    }

    override fun getRepositoriesPagingSource(
        username: String,
        type: String?,
        sort: String?,
        direction: String?,
        perPage: Int,
        page: Int
    ): GitHubRepositoryPagingSource =
        GitHubRepositoryPagingSource(api, username, type, sort, direction, perPage, page)
}