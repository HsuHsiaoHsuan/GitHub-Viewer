package idv.hsu.githubviewer.domain.usecase

import idv.hsu.githubviewer.domain.common.DomainResult
import idv.hsu.githubviewer.domain.model.User
import idv.hsu.githubviewer.domain.repository.GitHubRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserByIdUseCase @Inject constructor(
    private val repository: GitHubRepository
) {
    operator fun invoke(id: String): Flow<DomainResult<User>> =
        repository.getUserById(id)
}
