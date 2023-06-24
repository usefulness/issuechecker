package com.starter.issuechecker.resolvers.youtrack

import com.starter.issuechecker.IssueStatus
import com.starter.issuechecker.resolvers.StatusResolver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.http.GET
import retrofit2.http.Path
import java.net.URI

internal class YoutrackStatusResolver(
    private val service: YoutrackService,
) : StatusResolver {

    override fun handles(url: URI): Boolean = pattern.containsMatchIn(url.toString())

    override suspend fun resolve(url: URI): IssueStatus = withContext(Dispatchers.IO) {
        val issueId = pattern.find(url.toString())?.groups?.last()?.value
            ?: throw IllegalArgumentException("Couldn't parse $url")
        val response = service.getIssue(issueId = issueId)

        if (response.resolved == null) {
            IssueStatus.Open
        } else {
            IssueStatus.Closed
        }
    }

    companion object {
        private val pattern by lazy {
            "https?://(www.)?youtrack.jetbrains.com/issue/([^/]+)/?".toRegex()
        }
    }
}

internal interface YoutrackService {
    @GET("api/issues/{issueId}?fields=resolved,idReadable,summary")
    suspend fun getIssue(@Path("issueId") issueId: String): YoutrackIssue
}

@Serializable
internal data class YoutrackIssue(
    @SerialName(value = "idReadable") val idReadable: String,
    @SerialName(value = "summary") val summary: String,
    @SerialName(value = "resolved") val resolved: Long?,
)
