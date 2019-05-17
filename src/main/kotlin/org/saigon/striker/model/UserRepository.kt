package org.saigon.striker.model

import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Mono

interface UserRepository : ReactiveCrudRepository<UserEntity, String> {

    fun findByUsername(username: String): Mono<UserEntity>
}
