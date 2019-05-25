package org.saigon.striker.model

import org.springframework.data.mongodb.core.ReactiveMongoOperations
import org.springframework.data.mongodb.core.find
import org.springframework.data.mongodb.core.findById
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class UserRepository(val mongoOperations: ReactiveMongoOperations) {

    fun findByUsername(username: String): Mono<UserEntity> = mongoOperations.find<UserEntity>(
        Query(UserEntity::username isEqualTo username)
    ).singleOrEmpty()

    fun findById(id: String): Mono<UserEntity> = mongoOperations.findById(id)

    fun save(userEntity: UserEntity): Mono<UserEntity> = mongoOperations.save(userEntity)

    fun deleteById(id: String): Mono<Void> = mongoOperations.remove(id).then()
}
