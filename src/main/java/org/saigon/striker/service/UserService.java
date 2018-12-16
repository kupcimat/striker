package org.saigon.striker.service;

import org.saigon.striker.model.UserEntity;
import org.saigon.striker.model.UserRepository;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static org.apache.commons.lang3.Validate.notEmpty;
import static org.apache.commons.lang3.Validate.notNull;

@Service
public class UserService implements ReactiveUserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        notEmpty(username);
        return userRepository.findByUsername(username)
                .map(this::userEntityToUserDetails);
    }

    public Mono<UserEntity> createUser(UserEntity userEntity) {
        notNull(userEntity);
        return userRepository.save(userEntity.withPassword(
                passwordEncoder.encode(userEntity.getPassword())));
    }

    public Mono<UserEntity> getUser(String id) {
        notEmpty(id);
        return userRepository.findById(id);
    }

    public Mono<UserEntity> getUserByUsername(String username) {
        notEmpty(username);
        return userRepository.findByUsername(username);
    }

    public Mono<Void> deleteUser(String id) {
        notEmpty(id);
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
