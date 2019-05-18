package org.saigon.striker

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class StrikerApplication

fun main(args: Array<String>) {
    runApplication<StrikerApplication>(*args)
}
