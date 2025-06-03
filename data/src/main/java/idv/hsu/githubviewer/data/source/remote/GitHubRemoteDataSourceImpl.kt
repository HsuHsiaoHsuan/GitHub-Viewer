package idv.hsu.githubviewer.data.source.remote

import idv.hsu.githubviewer.data.ApiResultWrapper
import idv.hsu.githubviewer.data.dto.RepositoryDto
import idv.hsu.githubviewer.data.dto.UserDto
import idv.hsu.githubviewer.data.safeApiCall
import idv.hsu.githubviewer.data.source.GitHubRemoteDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GitHubRemoteDataSourceImpl @Inject constructor(
    private val api: GitHubApi
) : GitHubRemoteDataSource {
    override fun getUsers(since: Int, perPage: Int?): Flow<ApiResultWrapper<List<UserDto>>> =
        safeApiCall {
            api.getUsers(since, perPage)
        }

    override fun getUserById(id: String): Flow<ApiResultWrapper<UserDto>> =
        safeApiCall {
            api.getUserById(id)
        }

    override fun getRepositories(
        username: String,
        type: String?,
        sort: String?,
        direction: String?,
        perPage: Int?
    ): Flow<ApiResultWrapper<List<RepositoryDto>>> = safeApiCall {
        api.getUserRepositories(username, type, sort, direction, perPage)
    }
}