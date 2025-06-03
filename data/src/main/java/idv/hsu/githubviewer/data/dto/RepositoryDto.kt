package idv.hsu.githubviewer.data.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import idv.hsu.githubviewer.domain.model.Repository

@JsonClass(generateAdapter = true)
data class RepositoryDto(
    val id: Long,
    val name: String,
    @Json(name = "html_url") val htmlUrl: String,
    val description: String?,
    val language: String?,
    @Json(name = "stargazers_count") val stargazersCount: Int,
    @Json(name = "watchers_count") val watchersCount: Int,
    val fork: Boolean
)