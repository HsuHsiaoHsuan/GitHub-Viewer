package idv.hsu.githubviewer.domain.repository

import idv.hsu.githubviewer.domain.common.DomainResult
import idv.hsu.githubviewer.domain.model.Repository
import idv.hsu.githubviewer.domain.model.User
import kotlinx.coroutines.flow.Flow

interface GitHubRepository {
    fun getUsers(since: Int, perPage: Int?): Flow<DomainResult<List<User>>>
    fun getUserById(username: String): Flow<DomainResult<User>>
    fun getRepositories(
        username: String,
        type: String?,
        sort: String?,
        direction: String?,
        perPage: Int?
    ): Flow<DomainResult<List<Repository>>>
}