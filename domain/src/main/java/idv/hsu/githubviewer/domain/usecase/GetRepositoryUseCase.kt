package idv.hsu.githubviewer.domain.usecase

import androidx.paging.PagingData
import androidx.paging.filter
import idv.hsu.githubviewer.domain.model.GetRepositoryRequestParams
import idv.hsu.githubviewer.domain.model.Repository
import idv.hsu.githubviewer.domain.repository.GitHubRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetRepositoryUseCase @Inject constructor(
    private val repository: GitHubRepository
) {
    operator fun invoke(
        params: GetRepositoryRequestParams
    ): Flow<PagingData<Repository>> =
        repository.getRepositoriesStream(
            username = params.username,
            type = params.type,
            sort = params.sort,
            direction = params.direction,
            perPage = params.perPage,
            page = params.page
        ).map { pageData ->
            pageData.filter { repository -> !repository.fork }
        }
}