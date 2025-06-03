package idv.hsu.githubviewer.core

object AvatarUtils {
    private val avatarPlaceholders = listOf(
        R.drawable.avatar_placeholder_1,
        R.drawable.avatar_placeholder_2,
        R.drawable.avatar_placeholder_3,
        R.drawable.avatar_placeholder_4,
        R.drawable.avatar_placeholder_5,
        R.drawable.avatar_placeholder_6,
        R.drawable.avatar_placeholder_7,
        R.drawable.avatar_placeholder_8
    )

    private var lastIndex = -1

    fun getRandomAvatarPlaceholder(): Int {
        var newIndex: Int
        do {
            newIndex = avatarPlaceholders.indices.random()
        } while (newIndex == lastIndex)
        lastIndex = newIndex
        return avatarPlaceholders[newIndex]
    }
}