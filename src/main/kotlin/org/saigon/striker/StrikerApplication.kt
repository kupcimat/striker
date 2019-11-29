package org.saigon.striker

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

object Profiles {
    const val HEROKU: String = "heroku"
    const val NOT_HEROKU: String = "!$HEROKU"
}

@SpringBootApplication
@ConfigurationPropertiesScan
class StrikerApplication

fun main(args: Array<String>) {
    runApplication<StrikerApplication>(*args)
}
