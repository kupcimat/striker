package org.saigon.striker.service;

import org.saigon.striker.model.UserEntity;
import org.saigon.striker.model.UserRepository;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static org.apache.commons.lang3.Validate.notNull;

@Service
public class UserService implements ReactiveUserDetailsService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(this::userEntityToUserDetails);
    }

    public Mono<UserEntity> createUser(UserEntity userEntity) {
        notNull(userEntity);
        return userRepository.save(userEntity);
    }

    public Mono<UserEntity> getUser(String id) {
        return userRepository.findById(id);
    }

    public Mono<Void> deleteUser(String id) {
        return userRepository.deleteById(id);
    }

    private UserDetails userEntityToUserDetails(UserEntity userEntity) {
        return org.springframework.security.core.userdetails.User.builder()
                .username(userEntity.getUsername())
                .password(userEntity.getPassword())
                .roles(UserRoles.USER.toString())
                .build();
    }
}
