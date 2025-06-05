package idv.hsu.githubviewer.domain.usecase

import androidx.paging.PagingData
import idv.hsu.githubviewer.domain.model.User
import idv.hsu.githubviewer.domain.repository.GitHubRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserUseCase @Inject constructor(
    private val repository: GitHubRepository
) {
    operator fun invoke(since: Int, perPage: Int): Flow<PagingData<User>> =
        repository.getUsersStream(since, perPage)
}
