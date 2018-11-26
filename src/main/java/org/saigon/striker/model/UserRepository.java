package org.saigon.striker.model;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveCrudRepository<UserEntity, String> {

    Mono<UserEntity> findByUsername(String username);
}
