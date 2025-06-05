package idv.hsu.githubviewer.domain.model

class GetRepositoryRequestParams(
    val username: String,
    val type: String?,
    val sort: String?,
    val direction: String?,
    val perPage: Int,
    val page: Int
)
