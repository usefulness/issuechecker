package com.starter.issuechecker.resolvers.github

import com.starter.issuechecker.IssueStatus
import com.starter.issuechecker.resolvers.StatusResolver
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.http.GET
import retrofit2.http.Path
import java.net.URI

internal class GithubStatusResolver(private val service: GithubService) : StatusResolver {

    override suspend fun resolve(url: URI): IssueStatus {
        val result = handledPattern.find(url.toString())?.groups ?: throw IllegalArgumentException("Couldn't parse $url")
        val owner = result[OWNER]?.value ?: throw IllegalArgumentException("Couldn't get owner from $url")
        val repo = result[REPO]?.value ?: throw IllegalArgumentException("Couldn't get repo from $url")
        val issueId = result[ISSUE_ID]?.value ?: throw IllegalArgumentException("Couldn't get issueId from $url")
        val status = service.getIssue(owner, repo, issueId)

        return if (status.closedAt == null) {
            IssueStatus.Open
        } else {
            IssueStatus.Closed
        }
    }

    override fun handles(url: URI): Boolean = handledPattern.containsMatchIn(url.toString())

    companion object {

        private const val OWNER = 2
        private const val REPO = 3
        private const val ISSUE_ID = 5

        private val handledPattern by lazy {
            "https?://(www.)?github.com/([^/]+)/([^/]+)/(issues|pull)/([^/]+)/?".toRegex()
        }
    }
}

internal interface GithubService {
    @GET("repos/{owner}/{repo}/issues/{issueId}")
    suspend fun getIssue(@Path("owner") owner: String, @Path("repo") repo: String, @Path("issueId") issueId: String): GithubIssue
}

@Serializable
internal data class GithubIssue(
    @SerialName(value = "title") val title: String,
    @SerialName(value = "closed_at") val closedAt: String?,
)
