package org.saigon.striker.gradle

import io.ktor.client.request.*
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.*
import kotlinx.serialization.builtins.list

@Serializable
data class PullRequestCreate(
    val title: String,
    val head: String,
    val base: String
)

@Serializable
data class PullRequest(
    val number: Int,
    val title: String,
    val url: String
)

@Serializable
data class PullRequestList(
    val pullRequests: List<PullRequest>
) {
    @Serializer(PullRequestList::class)
    companion object : KSerializer<PullRequestList> {
        override val descriptor: SerialDescriptor = PrimitiveDescriptor("PullRequestList", PrimitiveKind.STRING)

        override fun deserialize(decoder: Decoder): PullRequestList {
            return PullRequestList(PullRequest.serializer().list.deserialize(decoder))
        }
    }
}

suspend fun existsPullRequest(githubToken: String, pullRequest: PullRequestCreate): Boolean {
    val response = withHttpClient {
        get<PullRequestList> {
            pullRequestUrl(pullRequest.head, pullRequest.base)
            authorization(githubToken)
        }
    }
    return response.pullRequests.isNotEmpty()
}

suspend fun createPullRequest(githubToken: String, pullRequest: PullRequestCreate) {
    if (existsPullRequest(githubToken, pullRequest)) {
        return
    }
    withHttpClient {
        post<PullRequest> {
            pullRequestUrl()
            authorization(githubToken)
            contentType(ContentType.Application.Json)
            body = pullRequest
        }
    }
}

private fun HttpRequestBuilder.pullRequestUrl(head: String? = null, base: String? = null) {
    url("https://api.github.com/repos/kupcimat/striker/pulls")
    parameter("head", head)
    parameter("base", base)
}

private fun HttpRequestBuilder.authorization(githubToken: String) {
    header("Authorization", "token $githubToken")
}
