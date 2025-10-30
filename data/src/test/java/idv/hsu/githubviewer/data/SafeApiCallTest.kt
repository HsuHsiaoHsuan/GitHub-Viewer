package idv.hsu.githubviewer.data

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import retrofit2.Response
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class SafeApiCallTest {

    @Test
    fun `safeApiCall emits success when response is successful`() = runTest {
        val response = Response.success("test")
        val flow = safeApiCall { response }

        val result = flow.first()
        assertTrue(result is ApiResultWrapper.Success)
        assertEquals("test", (result as ApiResultWrapper.Success).data)
    }

    @Test
    fun `safeApiCall emits network error on IOException`() = runTest {
        val flow = safeApiCall<String> { throw IOException("network error") }

        val result = flow.first()
        assertTrue(result is ApiResultWrapper.NetworkError)
    }

    @Test
    fun `safeApiCall emits error on HttpException`() = runTest {
        val errorResponse = Response.error<String>(400, "error".toResponseBody())
        val flow = safeApiCall<String> { errorResponse }

        val result = flow.first()
        assertTrue(result is ApiResultWrapper.Error)
        assertEquals(400, (result as ApiResultWrapper.Error).code)
    }

    @Test
    fun `safeApiCall emits error on unknown exception`() = runTest {
        val flow = safeApiCall<String> { throw IllegalStateException("unexpected") }

        val result = flow.first()
        assertTrue(result is ApiResultWrapper.Error)
    }
}