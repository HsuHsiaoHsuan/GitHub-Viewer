package idv.hsu.githubviewer.data.source

import idv.hsu.githubviewer.data.ApiResultWrapper
import idv.hsu.githubviewer.data.dto.RepositoryDto
import idv.hsu.githubviewer.data.dto.UserDto
import kotlinx.coroutines.flow.Flow

interface GitHubRemoteDataSource {
    fun getUsers(since: Int, perPage: Int?): Flow<ApiResultWrapper<List<UserDto>>>

    fun getUserById(username: String): Flow<ApiResultWrapper<UserDto>>

    fun getRepositories(
        username: String,
        type: String?,
        sort: String?,
        direction: String?,
        perPage: Int?
    ): Flow<ApiResultWrapper<List<RepositoryDto>>>
}