package idv.hsu.githubviewer.domain.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Repository(
    val id: Long,
    val name: String,
    val htmlUrl: String,
    val description: String,
    val language: String,
    val stargazersCount: Int,
    val watchersCount: Int,
    val fork: Boolean
)
