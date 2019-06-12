package org.saigon.striker.server

import com.charleskorn.kaml.Yaml
import kotlinx.serialization.Serializable

@Serializable
data class MockConfiguration(
    /**
     * Map of paths to methods.
     */
    val paths: Map<String, Map<String, MockResponse>> = mapOf()
)

@Serializable
data class MockResponse(
    val status: Int,
    val content: String,
    val headers: Map<String, String> = mapOf(),
    val cookies: Map<String, String> = mapOf()
)

fun yamlConfiguration(configurationFile: String): MockConfiguration {
    // TODO how to read file resources
    val yaml = MockConfiguration::class.java.getResource(configurationFile).readText()
    return Yaml.default.parse(MockConfiguration.serializer(), yaml)
}

fun mockConfiguration(configure: PathsBuilder.() -> Unit): MockConfiguration {
    return PathsBuilder().build(configure)
}

@Suppress("MemberVisibilityCanBePrivate")
class PathsBuilder {

    var paths = mutableMapOf<String, Map<String, MockResponse>>()

    fun path(name: String, builder: MethodsBuilder.() -> Unit) {
        paths[name] = MethodsBuilder().build(builder)
    }

    fun build(builder: PathsBuilder.() -> Unit): MockConfiguration {
        builder(this)
        return MockConfiguration(paths)
    }
}

@Suppress("MemberVisibilityCanBePrivate")
class MethodsBuilder {

    var methods = mutableMapOf<String, MockResponse>()

    fun method(name: String, builder: ResponseBuilder.() -> Unit) {
        methods[name] = ResponseBuilder().build(builder)
    }

    fun get(builder: ResponseBuilder.() -> Unit) {
        method("get", builder)
    }

    fun build(builder: MethodsBuilder.() -> Unit): Map<String, MockResponse> {
        builder(this)
        return methods
    }
}

@Suppress("MemberVisibilityCanBePrivate")
class ResponseBuilder {

    var status: Int = 200
    var content: String = ""
    var headers = mutableMapOf<String, String>()
    var cookies = mutableMapOf<String, String>()

    fun header(name: String, value: String) {
        headers[name] = value
    }

    fun cookie(name: String, value: String) {
        cookies[name] = value
    }

    fun build(builder: ResponseBuilder.() -> Unit): MockResponse {
        builder(this)
        return MockResponse(status, content, headers, cookies)
    }
}
