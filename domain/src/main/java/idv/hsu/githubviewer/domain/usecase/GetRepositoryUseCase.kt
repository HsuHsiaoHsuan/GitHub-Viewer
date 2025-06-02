package idv.hsu.githubviewer.domain.usecase

import idv.hsu.githubviewer.domain.repository.GitHubRepository
import javax.inject.Inject

class GetRepositoryUseCase @Inject constructor(
    private val repository: GitHubRepository
) {
    operator fun invoke(username: String) = repository.getRepositories(username)
}