package idv.hsu.githubviewer.data.repository

import androidx.paging.PagingData
import idv.hsu.githubviewer.data.ApiResultWrapper
import idv.hsu.githubviewer.data.dto.RepositoryDto
import idv.hsu.githubviewer.data.source.GitHubRemoteDataSource
import idv.hsu.githubviewer.domain.common.DomainResult
import idv.hsu.githubviewer.domain.model.GetRepositoryRequestParams
import idv.hsu.githubviewer.domain.model.User
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GitHubRepositoryImplTest {

    private val remoteDataSource = mockk<GitHubRemoteDataSource>()
    private lateinit var repository: GitHubRepositoryImpl

    @BeforeEach
    fun setup() {
        repository = GitHubRepositoryImpl(remoteDataSource)
    }

    @Test
    fun `getRepositories returns DomainResult Success`() = runTest {
        val params = GetRepositoryRequestParams(
            username = "fuyu",
            type = null,
            sort = null,
            direction = null,
            perPage = 30,
            page = 1
        )
        every {
            remoteDataSource.getRepositories(
                username = params.userName,
                type = null,
                sort = null,
                direction = null,
                perPage = params.pageSize,
                page = params.page
            )
        } returns flowOf(
            ApiResultWrapper.Success(
                listOf(
                    RepositoryDto(id = 1, name = "repo", description = "desc", ...)
            )
        )
        )

        val result = repository.getRepositories(params).first()
        assertTrue(result is DomainResult.Success)
    }

    @Test
    fun `getRepositories returns DomainResult Error`() = runTest {
        val params = GetRepositoryRequestParams("fuyu", 1, 30)
        every {
            remoteDataSource.getRepositories(
                username = params.userName,
                type = null,
                sort = null,
                direction = null,
                perPage = params.pageSize,
                page = params.page
            )
        } returns flowOf(
            ApiResultWrapper.Error(code = 404, errorMessage = "Not found")
        )

        val result = repository.getRepositories(params).first()
        assertTrue(result is DomainResult.Error)
    }
}
