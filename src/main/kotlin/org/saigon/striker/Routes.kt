package org.saigon.striker

import org.saigon.striker.handler.AgodaHandler
import org.saigon.striker.handler.UserHandler
import org.springframework.web.reactive.function.server.coRouter

fun routesAgoda(agodaHandler: AgodaHandler) = coRouter {
    GET("/api/agoda", agodaHandler::getHotel)
    GET("/api/agoda/search", agodaHandler::search)
}

fun routesUser(userHandler: UserHandler) = coRouter {
    POST("/api/admin/users", userHandler::createUser)
    GET("/api/admin/users/{userId}", userHandler::getUser)
    DELETE("/api/admin/users/{userId}", userHandler::deleteUser)
}
