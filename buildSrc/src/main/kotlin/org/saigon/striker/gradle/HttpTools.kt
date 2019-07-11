@file:UseExperimental(KtorExperimentalAPI::class, UnstableDefault::class)

package org.saigon.striker.gradle

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.util.KtorExperimentalAPI
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json

suspend fun <T> withHttpClient(block: suspend HttpClient.() -> T): T {
    val client = HttpClient(CIO) {
        install(JsonFeature) { serializer = KotlinxSerializer(Json.nonstrict) }
    }
    return client.use { block(it) }
}