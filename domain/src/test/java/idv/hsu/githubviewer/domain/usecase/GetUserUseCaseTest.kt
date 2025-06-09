package idv.hsu.githubviewer.domain.usecase

import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.recyclerview.widget.ListUpdateCallback
import idv.hsu.githubviewer.domain.model.User
import idv.hsu.githubviewer.domain.repository.GitHubRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@OptIn(ExperimentalCoroutinesApi::class)
class GetUserUseCaseTest {

    private lateinit var repository: GitHubRepository
    private lateinit var useCase: GetUserUseCase

    @BeforeEach
    fun setUp() {
        repository = mockk()
        useCase = GetUserUseCase(repository)
    }

    @Test
    fun `invoke emits PagingData with expected users`() = runTest {
        val dummy = listOf(
            User(
                login = "alice",
                id = 1,
                avatarUrl = "url",
                name = "Alice",
                followers = 1,
                following = 1
            ),
            User(
                login = "bob",
                id = 2,
                avatarUrl = "url2",
                name = "Bob",
                followers = 2,
                following = 2
            )
        )
        val pagingData = PagingData.from(dummy)
        coEvery { repository.getUsersStream(since = 0, perPage = 30) } returns flowOf(pagingData)

        val result: PagingData<User> = useCase.invoke(since = 0, perPage = 30).first()

        val differ = AsyncPagingDataDiffer(
            diffCallback = object : androidx.recyclerview.widget.DiffUtil.ItemCallback<User>() {
                override fun areItemsTheSame(oldItem: User, newItem: User) =
                    oldItem.id == newItem.id

                override fun areContentsTheSame(oldItem: User, newItem: User) = oldItem == newItem
            },
            updateCallback = NoopListCallback(),
            mainDispatcher = UnconfinedTestDispatcher(),
            workerDispatcher = UnconfinedTestDispatcher()
        )

        differ.submitData(result)
        advanceUntilIdle()
        assertEquals(dummy, differ.snapshot().items)
        assertEquals(2, differ.snapshot().size)
        assertEquals("alice", differ.snapshot()[0]?.login)
        assertEquals("bob", differ.snapshot()[1]?.login)
    }

    @Test
    fun `invoke returns error flow when repository throws`() = runTest {
        val error = RuntimeException("Network error")
        coEvery { repository.getUsersStream(0, 30) } returns flow { throw error }

        val flow: Flow<PagingData<User>> = useCase.invoke(0, 30)
        val thrown = assertThrows<RuntimeException> {
            flow.collect()
        }
        assertEquals("Network error", thrown.message)
    }
}

class NoopListCallback : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}