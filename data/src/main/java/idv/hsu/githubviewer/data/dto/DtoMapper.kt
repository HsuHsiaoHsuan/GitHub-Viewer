package idv.hsu.githubviewer.data.dto

import idv.hsu.githubviewer.domain.model.Repository
import idv.hsu.githubviewer.domain.model.User

fun RepositoryDto.toDomain(): Repository =
    Repository(
        id = id,
        name = name,
        htmlUrl = htmlUrl,
        description = description ?: "",
        language = language ?: "",
        stargazersCount = stargazersCount,
        watchersCount = watchersCount,
        fork = fork
    )

fun UserDto.toDomain(): User =
    User(
        login = login,
        id = id,
        avatarUrl = avatarUrl,
        name = name,
        followers = followers,
        following = following
    )