package idv.hsu.githubviewer.data.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import idv.hsu.githubviewer.domain.model.User

@JsonClass(generateAdapter = true)
data class UserDto(
    val login: String,
    val id: Long,
    @Json(name = "avatar_url") val avatarUrl: String,
    val name: String?,
    val followers: Int?,
    val following: Int?
)