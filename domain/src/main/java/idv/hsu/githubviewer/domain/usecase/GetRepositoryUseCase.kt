package idv.hsu.githubviewer.domain.usecase

import idv.hsu.githubviewer.domain.common.DomainResult
import idv.hsu.githubviewer.domain.model.Repository
import idv.hsu.githubviewer.domain.repository.GitHubRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRepositoryUseCase @Inject constructor(
    private val repository: GitHubRepository
) {
    operator fun invoke(
        username: String,
        type: String? = null,
        sort: String? = null,
        direction: String? = null,
        perPage: Int? = null
    ): Flow<DomainResult<List<Repository>>> =
        repository.getRepositories(username, type, sort, direction, perPage)
}