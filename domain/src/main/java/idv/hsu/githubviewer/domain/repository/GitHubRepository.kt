package idv.hsu.githubviewer.domain.repository

import idv.hsu.githubviewer.domain.common.ResultWrapper
import idv.hsu.githubviewer.domain.model.Repository
import idv.hsu.githubviewer.domain.model.User
import kotlinx.coroutines.flow.Flow

interface GitHubRepository {
    fun getUsers(): Flow<ResultWrapper<List<User>>>
    fun getRepositories(username: String): Flow<ResultWrapper<List<Repository>>>
}