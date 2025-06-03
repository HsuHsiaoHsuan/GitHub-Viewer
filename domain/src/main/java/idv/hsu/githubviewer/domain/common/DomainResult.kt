package idv.hsu.githubviewer.domain.common

sealed class DomainResult<out T> {
    data class Success<out T>(val data: T) : DomainResult<T>()
    data class Error(val code: Int? = null, val error: String? = null) :
        DomainResult<Nothing>()
}