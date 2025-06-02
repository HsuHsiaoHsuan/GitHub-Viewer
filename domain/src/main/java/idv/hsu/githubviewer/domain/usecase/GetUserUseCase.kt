package idv.hsu.githubviewer.domain.usecase

import idv.hsu.githubviewer.domain.repository.GitHubRepository
import javax.inject.Inject

class GetUserUseCase @Inject constructor(
    private val repository: GitHubRepository
) {
    operator fun invoke() = repository.getUsers()
}
