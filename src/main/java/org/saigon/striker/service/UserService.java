package org.saigon.striker.service;

import org.saigon.striker.model.UserEntity;
import org.saigon.striker.model.UserRepository;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import static org.apache.commons.lang3.Validate.notNull;

// TODO switch to reactive
@Service
public class UserService implements ReactiveUserDetailsService {

    public static final String ROLE_USER = "USER";

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        // TODO switch to mongo db
        return Mono.defer(() -> Mono.justOrEmpty(userRepository.findByUsername(username)))
                .subscribeOn(Schedulers.elastic())
                .map(this::userEntityToUserDetails);
    }

    public UserEntity createUser(UserEntity userEntity) {
        notNull(userEntity);
        return userRepository.save(userEntity);
    }

    public UserEntity getUser(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    public void deleteUser(long userId) {
        getUser(userId);
        userRepository.deleteById(userId);
    }

    private UserDetails userEntityToUserDetails(UserEntity userEntity) {
        return org.springframework.security.core.userdetails.User.builder()
                .username(userEntity.getUsername())
                .password(userEntity.getPassword())
                .roles(ROLE_USER)
                .build();
    }
}
