package idv.hsu.githubviewer.domain.usecase

import androidx.paging.PagingData
import androidx.paging.testing.asSnapshot
import idv.hsu.githubviewer.domain.model.GetRepositoryRequestParams
import idv.hsu.githubviewer.domain.model.Repository
import idv.hsu.githubviewer.domain.repository.GitHubRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class GetRepositoryUseCaseTest {

    private lateinit var repository: GitHubRepository
    private lateinit var useCase: GetRepositoryUseCase

    @BeforeEach
    fun setUp() {
        repository = mockk(relaxed = true)
        useCase = GetRepositoryUseCase(repository)
    }

    @Test
    fun `invoke returns PagingData with correct repositories on success`() = runTest {
        val params = GetRepositoryRequestParams(
            username = "testuser",
            type = "all",
            sort = "updated",
            direction = "desc",
            perPage = 10,
            page = 1
        )
        val repoList = listOf(
            Repository(
                id = 1, name = "Repo1",
                htmlUrl = "https://github.com/testuser/Repo1",
                description = "desc1",
                language = "Kotlin",
                stargazersCount = 10,
                watchersCount = 5,
                fork = false
            ),
            Repository(
                id = 2, name = "Repo2",
                htmlUrl = "https://github.com/testuser/Repo2",
                description = "desc2",
                language = "Java",
                stargazersCount = 20,
                watchersCount = 10,
                fork = true
            )
        )
        val pagingData = PagingData.from(repoList)
        coEvery {
            repository.getRepositoriesStream(
                username = params.username,
                type = params.type,
                sort = params.sort,
                direction = params.direction,
                perPage = params.perPage,
                page = params.page
            )
        } returns flow { emit(pagingData) }

        val result = useCase(params)
        val snapshot = result.asSnapshot()
        assertEquals(2, snapshot.size)
        assertEquals(repoList[0], snapshot[0])
        assertEquals(repoList[1], snapshot[1])
    }

    @Test
    fun `invoke throws exception when repository throws`() = runTest {
        val params = GetRepositoryRequestParams(
            username = "testuser",
            type = "all",
            sort = "updated",
            direction = "desc",
            perPage = 10,
            page = 1
        )
        val exception = RuntimeException("Network error")
        coEvery {
            repository.getRepositoriesStream(
                username = params.username,
                type = params.type,
                sort = params.sort,
                direction = params.direction,
                perPage = params.perPage,
                page = params.page
            )
        } returns flow { throw exception }

        val result = useCase(params)
        val thrown = assertThrows<RuntimeException> {
            result.asSnapshot()
        }
        assertEquals("Network error", thrown.message)
    }
}