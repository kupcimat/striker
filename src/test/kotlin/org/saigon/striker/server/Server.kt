package org.saigon.striker.server

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.log
import io.ktor.http.CookieEncoding
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.response.respondFile
import io.ktor.routing.Route
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.coroutines.runBlocking
import java.io.File
import java.util.concurrent.TimeUnit

fun withServerBlocking(configuration: MockConfiguration, action: suspend () -> Unit) {
    val server = startMockServer(configuration)
    try {
        runBlocking { action() }
    } finally {
        stopMockServer(server)
    }
}

fun startMockServer(configuration: MockConfiguration, port: Int = 8080): ApplicationEngine {
    return embeddedServer(Netty, port = port, module = configureMainModule(configuration)).start(wait = false)
}

fun stopMockServer(server: ApplicationEngine) {
    server.stop(gracePeriod = 1L, timeout = 1L, timeUnit = TimeUnit.SECONDS)
}

fun configureMainModule(configuration: MockConfiguration): Application.() -> Unit {
    return {
        routing {
            // log request matching
            trace { application.log.debug(it.buildText()) }
            mockServerRoutes(configuration)
        }
    }
}

fun Route.mockServerRoutes(configuration: MockConfiguration) {
    for ((path, methods) in configuration.paths) {
        for ((method, response) in methods) {
            route(path, parseMethod(method)) {
                handle {
                    call.response.status(HttpStatusCode.fromValue(response.status))
                    response.headers.forEach { call.response.headers.append(name = it.key, value = it.value) }
                    response.cookies.forEach {
                        call.response.cookies.append(name = it.key, value = it.value, encoding = CookieEncoding.RAW)
                    }
                    call.respondFile(File(javaClass.getResource(response.content).file))
                }
            }
        }
    }
}

fun parseMethod(method: String): HttpMethod {
    return HttpMethod.DefaultMethods.find { it.value == method.toUpperCase() }
        ?: throw IllegalArgumentException("Unsupported HTTP method: $method")
}
