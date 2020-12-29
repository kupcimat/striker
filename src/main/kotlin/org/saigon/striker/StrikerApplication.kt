package org.saigon.striker

import org.springframework.fu.kofu.reactiveWebApplication

val application = reactiveWebApplication {
    enable(dataConfig)
    enable(webConfig)
    enable(securityConfig)
}

fun main(args: Array<String>) {
    application.run(args)
}
