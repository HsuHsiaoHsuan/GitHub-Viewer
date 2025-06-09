package idv.hsu.githubviewer.data.source.remote

import androidx.paging.PagingSource
import androidx.paging.PagingState
import idv.hsu.githubviewer.data.dto.UserDto
import retrofit2.HttpException
import java.io.IOException

class GitHubUserPagingSource(
    private val gitHubApiService: GitHubApiService,
    private val since: Int
) : PagingSource<Int, UserDto>() {

    override fun getRefreshKey(state: PagingState<Int, UserDto>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.run {
                prevKey ?: nextKey
            }
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UserDto> {
        return try {
            val currentSinceId = params.key ?: since
            val response = gitHubApiService.getUsers(
                since = currentSinceId,
                perPage = params.loadSize
            )

            if (!response.isSuccessful) {
                return LoadResult.Error(HttpException(response))
            }

            val users = response.body().orEmpty()
            val nextKey = if (users.isEmpty()) {
                null
            } else {
                users.last().id.toInt()
            }

            LoadResult.Page(
                data = users,
                prevKey = null,
                nextKey = nextKey
            )
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        } catch (exception: IOException) {
            LoadResult.Error(exception)
        }
    }
}