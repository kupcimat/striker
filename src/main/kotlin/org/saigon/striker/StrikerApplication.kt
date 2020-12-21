package org.saigon.striker

import org.saigon.striker.config.MongoIndexCreator
import org.saigon.striker.controller.AgodaHandler
import org.saigon.striker.controller.UriTemplates
import org.saigon.striker.controller.UserHandler
import org.saigon.striker.model.UserRepository
import org.saigon.striker.service.AgodaService
import org.saigon.striker.service.UserService
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.boot.web.error.ErrorAttributeOptions
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes
import org.springframework.fu.kofu.configuration
import org.springframework.fu.kofu.mongo.reactiveMongodb
import org.springframework.fu.kofu.reactiveWebApplication
import org.springframework.fu.kofu.webflux.webFlux
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.coRouter

object Profiles {
    const val HEROKU: String = "heroku"
    const val NOT_HEROKU: String = "!$HEROKU"
}

val dataConfig = configuration {
    beans {
        bean<MongoIndexCreator>()
        bean<UserRepository>()
    }
    listener<ApplicationReadyEvent> {
        ref<MongoIndexCreator>().createIndices()
    }
    reactiveMongodb()
}

fun routesAgoda(agodaHandler: AgodaHandler) = coRouter {
    GET(UriTemplates.AGODA, agodaHandler::getHotel)
    GET(UriTemplates.AGODA_SEARCH, agodaHandler::search)
}

fun routesUser(userHandler: UserHandler) = coRouter {
    POST(UriTemplates.USERS, userHandler::createUser)
    GET(UriTemplates.USER, userHandler::getUser)
    DELETE(UriTemplates.USER, userHandler::deleteUser)
}

class MyErrorAttributes : DefaultErrorAttributes() {
    override fun getErrorAttributes(request: ServerRequest?, options: ErrorAttributeOptions?): MutableMap<String, Any> {
        return super.getErrorAttributes(request, ErrorAttributeOptions.of(ErrorAttributeOptions.Include.MESSAGE))
    }
}

val webConfig = configuration {
    beans {
        bean(::MyErrorAttributes, isPrimary = true)
        bean(WebClient::builder)
        bean<AgodaService>()
        bean<AgodaHandler>()
        bean<UserService>()
        bean<UserHandler>()
        bean(::routesAgoda)
        bean(::routesUser)
    }
    webFlux {
        codecs {
            string()
            jackson {
                dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
            }
        }
    }
}

val securityConfig = configuration {
    beans {
        bean(PasswordEncoderFactories::createDelegatingPasswordEncoder)
    }
}

val application = reactiveWebApplication {
    enable(dataConfig)
    enable(webConfig)
    enable(securityConfig)
}

fun main(args: Array<String>) {
    application.run(args)
}
