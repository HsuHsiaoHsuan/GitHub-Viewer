package idv.hsu.githubviewer.data.source

import idv.hsu.githubviewer.data.ApiResultWrapper
import idv.hsu.githubviewer.data.dto.RepositoryDto
import idv.hsu.githubviewer.data.dto.UserDto
import idv.hsu.githubviewer.domain.common.DomainResult
import kotlinx.coroutines.flow.Flow

interface GitHubRemoteDataSource {
    fun getUser(since: Int, perPage: Int?): Flow<ApiResultWrapper<List<UserDto>>>

    fun getRepositories(
        username: String,
        type: String?,
        sort: String?,
        direction: String?,
        perPage: Int?
    ): Flow<ApiResultWrapper<List<RepositoryDto>>>
}