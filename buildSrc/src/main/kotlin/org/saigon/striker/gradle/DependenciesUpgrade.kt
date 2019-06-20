@file:UseExperimental(UnstableDefault::class)

package org.saigon.striker.gradle

import io.ktor.client.HttpClient
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.get
import io.ktor.http.URLBuilder
import kotlinx.serialization.Serializable
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import java.io.File
import kotlin.text.RegexOption.IGNORE_CASE

@Serializable
data class MavenSearchResult(
    val response: MavenArtifacts
) {
    val latestVersions = response.docs.map(MavenArtifact::v)
}

@Serializable
data class MavenArtifacts(
    val docs: List<MavenArtifact>
)

@Serializable
data class MavenArtifact(
    val g: String,
    val a: String,
    val v: String
)

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
    println("Plugin $resolvedId current=$version latest=${latestVersion ?: "NOT_FOUND"}")

    return line.replace(version, latestVersion ?: version)
}

suspend fun upgradeDependencyVersion(line: String, matchResult: MatchResult): String {
    val (group, name, version) = matchResult.destructured

    val latestVersion = findLatestDependencyVersion(group, name)
    println("Dependency $group:$name current=$version latest=${latestVersion ?: "NOT_FOUND"}")

    return line.replace(version, latestVersion ?: version)
}

suspend fun findLatestPluginVersion(pluginId: String): String? {
    val result = withHttpClient {
        it.get<String>(createGradleSearchUri(pluginId))
    }

    val latestVersionRegex = Regex("<span class='latest-version'>(.+)</span>")
    return latestVersionRegex.find(result)?.groupValues?.get(1)
}

suspend fun findLatestDependencyVersion(group: String, name: String): String? {
    val result = withHttpClient {
        it.get<MavenSearchResult>(createMavenSearchUri(group, name))
    }

    return result.latestVersions
        .filter(::isStableVersion)
        .firstOrNull(::isCompatibleVersion)
}

suspend fun <T> withHttpClient(block: suspend (HttpClient) -> T): T {
    val client = HttpClient {
        install(JsonFeature) { serializer = KotlinxSerializer(Json.nonstrict) }
    }
    return client.use { block(it) }
}

fun isStableVersion(version: String): Boolean {
    val versionPatterns = listOf("alpha", "beta", "m[0-9]*", "rc[0-9]*", "eap")
    return versionPatterns.none { it.toRegex(IGNORE_CASE).containsMatchIn(version) }
}

fun isCompatibleVersion(version: String): Boolean {
    val versionPatterns = listOf("kotlin12", "groovy-2\\.4")
    return versionPatterns.none { it.toRegex(IGNORE_CASE).containsMatchIn(version) }
}

fun createGradleSearchUri(pluginId: String): String {
    return URLBuilder("https://plugins.gradle.org/search").apply {
        parameters.append("term", pluginId)
    }.buildString()
}

fun createMavenSearchUri(group: String, name: String, rows: Int = 5): String {
    return URLBuilder("https://search.maven.org/solrsearch/select").apply {
        parameters.append("q", """g:"$group" AND a:"$name"""")
        parameters.append("rows", rows.toString())
        parameters.append("core", "gav")
        parameters.append("wt", "json")
    }.buildString()
}
