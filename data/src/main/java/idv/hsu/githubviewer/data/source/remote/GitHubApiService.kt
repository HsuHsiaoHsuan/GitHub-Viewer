package idv.hsu.githubviewer.data.source.remote

import idv.hsu.githubviewer.data.dto.RepositoryDto
import idv.hsu.githubviewer.data.dto.UserDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GitHubApiService {

    /**
     * https://docs.github.com/en/rest/users/users?apiVersion=2022-11-28#list-users
     */
    @GET("users")
    suspend fun getUsers(
        @Query("since") since: Int = 0,
        @Query("per_page") perPage: Int? = null
    ): Response<List<UserDto>>

    /**
     * https://docs.github.com/en/rest/users/users?apiVersion=2022-11-28#get-a-user-using-their-id
     */
    @GET("/user/{id}")
    suspend fun getUserById(
        @Path("id") id: String
    ): Response<UserDto>

    /**
     * https://docs.github.com/en/rest/repos/repos?apiVersion=2022-11-28#list-repositories-for-a-user
     */

    @GET("/users/{username}/repos")
    suspend fun getUserRepositories(
        @Path("username") username: String,
        @Query("type") type: String? = null,
        @Query("sort") sort: String? = null,
        @Query("direction") direction: String? = null,
        @Query("per_page") perPage: Int? = null,
        @Query("page") page: Int? = null
    ): Response<List<RepositoryDto>>
}