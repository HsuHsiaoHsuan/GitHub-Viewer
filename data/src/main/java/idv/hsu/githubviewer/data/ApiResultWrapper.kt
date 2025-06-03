package idv.hsu.githubviewer.data

sealed class ApiResultWrapper<out T> {
    data class Success<out T>(val data: T) : ApiResultWrapper<T>()
    data class Error(val code: Int? = null, val error: String? = null) :
        ApiResultWrapper<Nothing>()

    data object NetworkError : ApiResultWrapper<Nothing>()
}