package idv.hsu.githubviewer.data.dto

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class DtoMapperTest {

    @Test
    fun `UserDto maps correctly to User`() {
        val dto = UserDto(
            login = "freeman",
            id = 1,
            avatarUrl = "url",
            name = null,
            followers = null,
            following = null
        )
        val domain = dto.toDomain()

        assertEquals("freeman", domain.login)
        assertEquals(1, domain.id)
        assertEquals("url", domain.avatarUrl)
        assertEquals(null, domain.name)
        assertEquals(null, domain.followers)
        assertEquals(null, domain.following)
    }

    @Test
    fun `RepositoryDto maps correctly to Repository`() {
        val dto = RepositoryDto(
            id = 2,
            name = "test repo",
            htmlUrl = "url",
            stargazersCount = 1,
            watchersCount = 2,
            fork = false,
            description = null,
            language = null
        )
        val domain = dto.toDomain()

        assertEquals(2, domain.id)
        assertEquals("test repo", domain.name)
        assertEquals("url", domain.htmlUrl)
        assertEquals(1, domain.stargazersCount)
        assertEquals(2, domain.watchersCount)
        assertEquals(false, domain.fork)
        assertEquals("", domain.description)
        assertEquals("", domain.language)
    }
}