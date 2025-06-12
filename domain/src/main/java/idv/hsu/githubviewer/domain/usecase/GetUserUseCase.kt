package idv.hsu.githubviewer.domain.usecase

import android.util.Log
import androidx.paging.PagingData
import idv.hsu.githubviewer.domain.model.User
import idv.hsu.githubviewer.domain.repository.GitHubRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserUseCase @Inject constructor(
    private val repository: GitHubRepository
) {
    operator fun invoke(since: Int, perPage: Int): Flow<PagingData<User>> {
        Log.e("GetUserUseCase", "invoke called with since: $since, perPage: $perPage")
        return repository.getUsersStream(since, perPage)
    }
}
