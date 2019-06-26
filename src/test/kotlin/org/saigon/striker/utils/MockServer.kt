package org.saigon.striker.utils

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.log
import io.ktor.http.CookieEncoding
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.response.respondFile
import io.ktor.routing.*
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.applicationEngineEnvironment
import io.ktor.server.engine.connector
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.util.pipeline.ContextDsl
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.util.concurrent.TimeUnit

val logger: Logger = LoggerFactory.getLogger("MockServer")

fun startMockServer(host: String = "0.0.0.0", port: Int = 8080): ApplicationEngine {
    logger.info("Starting server at $host:$port")
    val environment = applicationEngineEnvironment {
        connector {
            this.host = host
            this.port = port
        }
    }
    return embeddedServer(Netty, environment).start(wait = false)
}

fun stopMockServer(server: ApplicationEngine) {
    logger.info("Stopping server")
    server.stop(gracePeriod = 1L, timeout = 1L, timeUnit = TimeUnit.SECONDS)
}

fun ApplicationEngine.configure(builder: PathsBuilder.() -> Unit) {
    configure(mockConfiguration(builder))
}

fun ApplicationEngine.configure(configuration: MockConfiguration) {
    logger.info("Applying $configuration")
    // Reload application environment
    environment.stop()
    environment.start()
    // Register application module
    application.mainModule(configuration)
}

private fun Application.mainModule(configuration: MockConfiguration) {
    routing {
        // Log request matching
        trace { application.log.debug(it.buildText()) }
        mockRoutes(configuration)
    }
}

private fun Route.mockRoutes(configuration: MockConfiguration) {
    for ((path, methods) in configuration.paths) {
        for ((method, response) in methods) {
            route(path, parseMethod(method)) {
                params(response.queryParams) {
                    handle {
                        call.response.status(HttpStatusCode.fromValue(response.status))
                        response.headers.forEach { call.response.headers.append(it.key, it.value) }
                        response.cookies.forEach { call.response.cookies.append(it.key, it.value, CookieEncoding.RAW) }
                        call.respondFile(File(javaClass.getResource("/${response.content}").file))
                    }
                }
            }
        }
    }
}

private fun parseMethod(method: String): HttpMethod {
    return HttpMethod.DefaultMethods.find { it.value == method.toUpperCase() }
        ?: throw IllegalArgumentException("Unsupported HTTP method: $method")
}

@ContextDsl
private fun Route.params(queryParameters: Map<String, String>, build: Route.() -> Unit): Route {
    val selector = ConstantParametersRouteSelector(queryParameters)
    return createChild(selector).apply(build)
}

private class ConstantParametersRouteSelector(val queryParameters: Map<String, String>) :
    RouteSelector(RouteSelectorEvaluation.qualityConstant) {

    override fun evaluate(context: RoutingResolveContext, segmentIndex: Int): RouteSelectorEvaluation {
        val callContainsAllParameters = queryParameters.all { context.call.parameters.contains(it.key, it.value) }
        if (callContainsAllParameters) {
            return RouteSelectorEvaluation.Constant
        }
        return RouteSelectorEvaluation.Failed
    }

    override fun toString(): String = "[${queryParameters.entries.joinToString()}]"
}
