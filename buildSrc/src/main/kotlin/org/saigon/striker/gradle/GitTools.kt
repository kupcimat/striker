package org.saigon.striker.gradle

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.Status
import org.eclipse.jgit.transport.URIish
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import java.io.File
import java.time.Instant

data class GitAuthor(
    val name: String,
    val email: String
)

data class GitCommit(
    val hash: String,
    val time: Instant? = null
)

data class GitCredentials(
    val httpsUri: String,
    val username: String,
    val password: String
)

fun gitStatus(directory: File): Status {
    return withGit(directory) {
        status()
            .call()
    }
}

fun gitLog(directory: File, maxCount: Int): List<GitCommit> {
    return withGit(directory) {
        log()
            .setMaxCount(maxCount)
            .call()
            .map { commit -> GitCommit(commit.name, Instant.ofEpochSecond(commit.commitTime.toLong())) }
    }
}

fun gitCheckout(directory: File, branch: String) {
    withGit(directory) {
        val createBranch = branchList()
            .call()
            .none { ref -> ref.name.contains(branch) }
        checkout()
            .setName(branch)
            .setCreateBranch(createBranch)
            .call()
    }
}

fun gitCommit(directory: File, message: String, author: GitAuthor) {
    withGit(directory) {
        commit()
            .setAll(true)
            .setMessage(message)
            .setCommitter(author.name, author.email)
            .call()
    }
}

fun gitPush(directory: File, branch: String, credentials: GitCredentials) {
    withGit(directory) {
        remoteAdd()
            .setName("https-remote")
            .setUri(URIish(credentials.httpsUri))
            .call()
        push()
            .add(branch)
            .setRemote("https-remote")
            .setCredentialsProvider(UsernamePasswordCredentialsProvider(credentials.username, credentials.password))
            .call()
    }
}

private fun <T> withGit(directory: File, block: Git.() -> T): T {
    val git = Git.open(directory)
    return git.use(block)
}
