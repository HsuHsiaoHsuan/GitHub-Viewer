package idv.hsu.githubviewer.data.source.remote

import idv.hsu.githubviewer.data.dto.RepositoryDto
import idv.hsu.githubviewer.data.dto.UserDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface GitHubApi {

    /**
     * https://docs.github.com/en/rest/users/users?apiVersion=2022-11-28#list-users
     */
    @Headers(
        "Accept: application/vnd.github+json",
        "X-GitHub-Api-Version: 2022-11-28",
    )
    @GET("users")
    suspend fun getUsers(
        @Query("since") since: Int = 0,
        @Query("per_page") perPage: Int? = null
    ): Response<List<UserDto>>


    /**
     * https://docs.github.com/en/rest/repos/repos?apiVersion=2022-11-28#list-repositories-for-a-user
     */
    @Headers(
        "Accept: application/vnd.github+json",
        "X-GitHub-Api-Version: 2022-11-28",
    )
    @GET("/users/{username}/repos")
    suspend fun getUserRepositories(
        @Path("username") username: String,
        @Query("type") type: String? = null,
        @Query("sort") sort: String? = null,
        @Query("direction") direction: String? = null, // "asc" or "desc"
        @Query("per_page") perPage: Int? = null
    ): Response<List<RepositoryDto>>
}