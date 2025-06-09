package idv.hsu.githubviewer.domain.repository

import androidx.paging.PagingData
import idv.hsu.githubviewer.domain.common.DomainResult
import idv.hsu.githubviewer.domain.model.Repository
import idv.hsu.githubviewer.domain.model.User
import kotlinx.coroutines.flow.Flow

interface GitHubRepository {
    @Deprecated("Use 'getUsersStream' to get paged data")
    fun getUsers(since: Int, perPage: Int?): Flow<DomainResult<List<User>>>
    fun getUsersStream(since: Int, perPage: Int): Flow<PagingData<User>>
    fun getUserById(username: String): Flow<DomainResult<User>>
    @Deprecated("Use 'getRepositoriesStream' to get paged data")
    fun getRepositories(
        username: String,
        type: String?,
        sort: String?,
        direction: String?,
        perPage: Int?
    ): Flow<DomainResult<List<Repository>>>
    fun getRepositoriesStream(
        username: String,
        type: String?,
        sort: String?,
        direction: String?,
        perPage: Int,
        page: Int
    ): Flow<PagingData<Repository>>
}