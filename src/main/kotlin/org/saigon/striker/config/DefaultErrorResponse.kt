package org.saigon.striker.config

import org.springframework.boot.web.reactive.error.DefaultErrorAttributes
import org.springframework.web.reactive.function.server.ServerRequest
import java.time.Instant

class DefaultErrorResponse : DefaultErrorAttributes() {

    override fun getErrorAttributes(request: ServerRequest, includeStackTrace: Boolean): Map<String, Any> {
        return super.getErrorAttributes(request, includeStackTrace).also {
            it["timestamp"] = Instant.now()
        }
    }
}
