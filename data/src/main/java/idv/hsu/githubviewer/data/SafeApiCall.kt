package idv.hsu.githubviewer.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okio.IOException
import retrofit2.HttpException
import retrofit2.Response

inline fun <T> safeApiCall(
    crossinline apiCall: suspend () -> Response<T>
): Flow<ApiResultWrapper<T>> = flow<ApiResultWrapper<T>> {
    val response = apiCall()
    if (response.isSuccessful) emit(ApiResultWrapper.Success(response.body()!!))
    else throw HttpException(response)
}.catch { throwable ->
    val result = when (throwable) {
        is IOException -> ApiResultWrapper.NetworkError
        is HttpException -> {
            val code = throwable.code()
            val errorResponse = throwable.response()?.errorBody()?.string() ?: "No Error body"
            ApiResultWrapper.Error(code, errorResponse)
        }

        else -> ApiResultWrapper.Error(null, throwable.message)
    }
    emit(result)
}.flowOn(Dispatchers.IO)