package org.saigon.striker.service;

import org.saigon.striker.model.UserEntity;
import org.saigon.striker.model.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static java.lang.String.format;
import static org.apache.commons.lang3.Validate.notNull;

@Service
public class UserService implements UserDetailsService {

    public static final String ROLE_USER = "USER";

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(format("Username '%s' not found", username)));

        return org.springframework.security.core.userdetails.User.builder()
                .username(userEntity.getUsername())
                .password(userEntity.getPassword())
                .roles(ROLE_USER)
                .build();
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
}
