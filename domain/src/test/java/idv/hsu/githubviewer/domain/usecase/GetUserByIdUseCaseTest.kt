package idv.hsu.githubviewer.domain.usecase

import idv.hsu.githubviewer.domain.common.DomainResult
import idv.hsu.githubviewer.domain.model.User
import idv.hsu.githubviewer.domain.repository.GitHubRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetUserByIdUseCaseTest {

    private lateinit var repository: GitHubRepository
    private lateinit var useCase: GetUserByIdUseCase

    @BeforeEach
    fun setUp() {
        repository = mockk()
        useCase = GetUserByIdUseCase(repository)
    }

    @Test
    fun `invoke returns expected DomainResult Success User`() = runTest {
        val userId = "123"
        val user = User(
            login = "testuser",
            id = 123,
            avatarUrl = "avatarUrl",
            name = "Test User",
            followers = 10,
            following = 5
        )
        val expected = DomainResult.Success(user)
        coEvery { repository.getUserById(userId) } returns flowOf(expected)

        val result = useCase.invoke(userId)
        assertEquals(expected, result.first())
    }

    @Test
    fun `invoke returns expected DomainResult Failure`() = runTest {
        val userId = "not_found"
        val expected = DomainResult.Error(code = 123, error = "User not found")
        coEvery { repository.getUserById(userId) } returns flowOf(expected)

        val result = useCase.invoke(userId)
        val actual = result.first()
        assert(actual is DomainResult.Error)
        assertEquals(expected.code, (actual as DomainResult.Error).code)
        assertEquals(expected.error, (actual as DomainResult.Error).error)
    }
}