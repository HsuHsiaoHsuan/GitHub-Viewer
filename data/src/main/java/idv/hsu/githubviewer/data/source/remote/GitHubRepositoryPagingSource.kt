package idv.hsu.githubviewer.data.source.remote

import androidx.paging.PagingSource
import androidx.paging.PagingState
import idv.hsu.githubviewer.core.DEFAULT_START_PAGE_INDEX
import idv.hsu.githubviewer.data.dto.RepositoryDto
import retrofit2.HttpException
import java.io.IOException

class GitHubRepositoryPagingSource(
    private val gitHubApiService: GitHubApiService,
    private val username: String,
    private val type: String?,
    private val sort: String?,
    private val direction: String?,
    private val perPage: Int,
    private val page: Int
) : PagingSource<Int, RepositoryDto>() {

    override fun getRefreshKey(state: PagingState<Int, RepositoryDto>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, RepositoryDto> {
        return try {
            val currentPageNumber = params.key ?: page
            val response = gitHubApiService.getUserRepositories(
                username, type, sort, direction, perPage, currentPageNumber
            )

            if (!response.isSuccessful) {
                return LoadResult.Error(HttpException(response))
            }

            val repositories = response.body().orEmpty()
            val nextKey = if (repositories.isEmpty()) {
                null
            } else {
                currentPageNumber + 1
            }

            LoadResult.Page(
                data = repositories,
                prevKey = if (currentPageNumber == DEFAULT_START_PAGE_INDEX) null else currentPageNumber - 1,
                nextKey = nextKey
            )
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        } catch (exception: IOException) {
            LoadResult.Error(exception)
        }
    }
}