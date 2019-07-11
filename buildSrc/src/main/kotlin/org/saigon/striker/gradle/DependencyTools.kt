package org.saigon.striker.gradle

import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.serialization.*
import kotlinx.serialization.internal.StringDescriptor
import java.io.File
import kotlin.text.RegexOption.IGNORE_CASE

@Serializable
data class MavenArtifact(
    val owner: String,
    val versions: List<String>
)

@Serializable
data class MavenSearchResult(
    val artifacts: List<MavenArtifact>
) {
    @Serializer(MavenSearchResult::class)
    companion object : KSerializer<MavenSearchResult> {
        override val descriptor: SerialDescriptor = StringDescriptor.withName("MavenSearchResult")

        override fun deserialize(decoder: Decoder): MavenSearchResult {
            return MavenSearchResult(MavenArtifact.serializer().list.deserialize(decoder))
        }
    }
}

suspend fun upgradeVersions(buildFile: File) {
    val pluginRegex = Regex("""(id|kotlin)\("(.+)"\) version "(.+)"""")
    val dependencyRegex = Regex("""dependency\("(.+):(.+):(.+)"\)""")

    val fileContent = buildFile.readLines()
    buildFile.writeText("")

    for (line in fileContent) {
        val newLine = when {
            pluginRegex.containsMatchIn(line) -> upgradePluginVersion(line, pluginRegex.find(line)!!)
            dependencyRegex.containsMatchIn(line) -> upgradeDependencyVersion(line, dependencyRegex.find(line)!!)
            else -> line
        }
        buildFile.appendText("$newLine\n")
    }
}

suspend fun upgradePluginVersion(line: String, matchResult: MatchResult): String {
    val (type, id, version) = matchResult.destructured
    val resolvedId = if (type == "kotlin") "org.jetbrains.kotlin.$id" else id

    val latestVersion = findLatestPluginVersion(resolvedId)
    val updateVersion = if ((latestVersion != null) && (latestVersion > version)) latestVersion else version
    println("Plugin $resolvedId current=$version latest=${latestVersion ?: "NOT_FOUND"}")

    return line.replace(version, updateVersion)
}

suspend fun upgradeDependencyVersion(line: String, matchResult: MatchResult): String {
    val (group, name, version) = matchResult.destructured

    val latestVersion = findLatestDependencyVersion(group, name)
    println("Dependency $group:$name current=$version latest=${latestVersion ?: "NOT_FOUND"}")

    return line.replace(version, latestVersion ?: version)
}

suspend fun findLatestPluginVersion(pluginId: String): String? {
    val result = withHttpClient {
        get<String>("https://plugins.gradle.org/search") {
            parameter("term", pluginId)
        }
    }

    val latestVersionRegex = Regex("<span class='latest-version'>(.+)</span>")
    return latestVersionRegex.find(result)?.groupValues?.get(1)
}

suspend fun findLatestDependencyVersion(group: String, name: String): String? {
    val result = withHttpClient {
        get<MavenSearchResult>("https://api.bintray.com/search/packages/maven") {
            parameter("g", group)
            parameter("a", name)
        }
    }

    return findPreferredArtifact(result)?.versions
        ?.filter(::isStableVersion)
        ?.firstOrNull(::isCompatibleVersion)
}

fun findPreferredArtifact(result: MavenSearchResult): MavenArtifact? {
    val preferredRepoOwners = listOf("kotlin", "groovy", "bintray")
    return preferredRepoOwners.fold(null as MavenArtifact?) { selectedArtifact, preferredOwner ->
        selectedArtifact ?: result.artifacts.find { it.owner == preferredOwner }
    }
}

fun isStableVersion(version: String): Boolean {
    val versionPatterns = listOf("alpha", "beta", "m[0-9]*", "rc[0-9]*", "eap")
    return versionPatterns.none { it.toRegex(IGNORE_CASE).containsMatchIn(version) }
}

fun isCompatibleVersion(version: String): Boolean {
    val versionPatterns = listOf("kotlin12", "groovy-2\\.4")
    return versionPatterns.none { it.toRegex(IGNORE_CASE).containsMatchIn(version) }
}
