package idv.hsu.githubviewer.domain.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class User(
    val login: String,
    val id: Long,
    val avatarUrl: String,
    val name: String,
    val fullName: String,
    val followers: Int,
    val following: Int
)
