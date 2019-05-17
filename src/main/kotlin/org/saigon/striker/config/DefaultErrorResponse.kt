package org.saigon.striker.config

import org.springframework.boot.web.reactive.error.DefaultErrorAttributes
import org.springframework.validation.BindingResult
import org.springframework.web.reactive.function.server.ServerRequest
import java.time.Instant

class DefaultErrorResponse : DefaultErrorAttributes() {

    override fun getErrorAttributes(request: ServerRequest, includeStackTrace: Boolean): Map<String, Any> {
        val errorResponse = super.getErrorAttributes(request, includeStackTrace)
        val error = getError(request)

        if (error is BindingResult) {
            errorResponse.remove("errors")
            errorResponse["message"] = createValidationErrorMessage(error)
        }
        errorResponse["timestamp"] = Instant.now()

        return errorResponse
    }

    private fun createValidationErrorMessage(validationResult: BindingResult): String {
        val fieldErrors = validationResult.fieldErrors.map {
            "Error in object '${it.objectName}' on field '${it.field}': ${it.defaultMessage}"
        }
        val globalErrors = validationResult.globalErrors.map {
            "Error in object '${it.objectName}': ${it.defaultMessage}"
        }

        return (fieldErrors + globalErrors).joinToString(separator = ". ")
    }
}
