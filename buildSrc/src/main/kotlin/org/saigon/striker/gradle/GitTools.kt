package org.saigon.striker.gradle

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.Status
import java.io.File
import java.time.Instant

data class GitCommit(
    val hash: String,
    val time: Instant? = null
)

fun getBuildVersion(directory: File): GitCommit {
    if (gitStatus(directory).isClean) {
        return gitLog(directory, maxCount = 1).first()
    }
    return GitCommit(hash = "development")
}

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

private fun <T> withGit(directory: File, block: Git.() -> T): T {
    val git = Git.open(directory)
    return git.use(block)
}
