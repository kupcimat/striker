package org.saigon.striker

import org.saigon.striker.config.MongoIndexCreator
import org.saigon.striker.handler.AgodaHandler
import org.saigon.striker.handler.UserHandler
import org.saigon.striker.model.UserRepository
import org.saigon.striker.service.AgodaService
import org.saigon.striker.service.UserService
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.fu.kofu.configuration
import org.springframework.fu.kofu.mongo.reactiveMongodb
import org.springframework.fu.kofu.webflux.webFlux
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.web.reactive.function.client.WebClient
import java.util.*

object Profiles {
    const val TEST: String = "test"
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
    reactiveMongodb {
        if (profiles.contains(Profiles.TEST)) {
            embedded()
        }
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
                timeZone = TimeZone.getTimeZone("UTC")
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
