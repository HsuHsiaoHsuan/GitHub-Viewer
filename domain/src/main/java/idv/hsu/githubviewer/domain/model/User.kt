package idv.hsu.githubviewer.domain.model

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
data class User(
    val login: String,
    val id: Long,
    val avatarUrl: String,
    val name: String?,
    val followers: Int?,
    val following: Int?
) : Parcelable
