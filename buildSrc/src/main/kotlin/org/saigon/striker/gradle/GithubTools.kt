package org.saigon.striker.gradle

import io.ktor.client.request.*
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.*

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

suspend fun existsPullRequest(githubToken: String, pullRequest: PullRequestCreate): Boolean {
    val pullRequests = withHttpClient {
        get<List<PullRequest>> {
            pullRequestUrl(pullRequest.head, pullRequest.base)
            authorization(githubToken)
        }
    }
    return pullRequests.isNotEmpty()
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
