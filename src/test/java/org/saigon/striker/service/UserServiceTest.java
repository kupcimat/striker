package org.saigon.striker.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.saigon.striker.model.UserEntity;
import org.saigon.striker.model.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

    private static final String ID = "userId";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final UserEntity USER_ENTITY = new UserEntity(ID, USERNAME, PASSWORD);

    @Captor
    private ArgumentCaptor<UserEntity> userEntityCaptor;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserService userService;

    @Test
    public void findByUsername() {
        when(userRepository.findByUsername(USERNAME)).thenReturn(Mono.just(USER_ENTITY));

        StepVerifier.create(userService.findByUsername(USERNAME))
                .assertNext(userDetails -> {
                    assertThat(userDetails.getUsername()).isEqualTo(USERNAME);
                    assertThat(userDetails.getPassword()).isEqualTo(PASSWORD);
                    assertThat(userDetails.getAuthorities())
                            .extracting(GrantedAuthority::getAuthority).containsOnly("ROLE_USER");
                })
                .verifyComplete();
    }

    @Test
    public void findByUsernameNotFound() {
        when(userRepository.findByUsername(USERNAME)).thenReturn(Mono.empty());

        StepVerifier.create(userService.findByUsername(USERNAME))
                .verifyComplete();
    }

    @Test
    public void findByUsernameInvalid() {
        assertThatNullPointerException().isThrownBy(() -> userService.findByUsername(null));
        assertThatIllegalArgumentException().isThrownBy(() -> userService.findByUsername(""));
    }

    @Test
    public void createUser() {
        when(passwordEncoder.encode(PASSWORD)).thenReturn("encodedPassword");
        when(userRepository.save(any())).thenReturn(Mono.just(USER_ENTITY));

        StepVerifier.create(userService.createUser(USER_ENTITY))
                .assertNext(this::assertUserEntity)
                .verifyComplete();

        verify(userRepository).save(userEntityCaptor.capture());
        assertThat(userEntityCaptor.getValue().getUsername()).isEqualTo(USERNAME);
        assertThat(userEntityCaptor.getValue().getPassword()).isEqualTo("encodedPassword");
    }

    @Test
    public void createUserInvalid() {
        assertThatNullPointerException().isThrownBy(() -> userService.createUser(null));
    }

    @Test
    public void getUser() {
        when(userRepository.findById(ID)).thenReturn(Mono.just(USER_ENTITY));

        StepVerifier.create(userService.getUser(ID))
                .assertNext(this::assertUserEntity)
                .verifyComplete();
    }

    @Test
    public void getUserNotFound() {
        when(userRepository.findById(ID)).thenReturn(Mono.empty());

        StepVerifier.create(userService.getUser(ID))
                .verifyComplete();
    }

    @Test
    public void getUserInvalid() {
        assertThatNullPointerException().isThrownBy(() -> userService.getUser(null));
        assertThatIllegalArgumentException().isThrownBy(() -> userService.getUser(""));
    }

    @Test
    public void deleteUser() {
        when(userRepository.deleteById(ID)).thenReturn(Mono.empty());

        StepVerifier.create(userService.deleteUser(ID))
                .verifyComplete();
    }

    @Test
    public void deleteUserInvalid() {
        assertThatNullPointerException().isThrownBy(() -> userService.deleteUser(null));
        assertThatIllegalArgumentException().isThrownBy(() -> userService.deleteUser(""));
    }

    private void assertUserEntity(UserEntity userEntity) {
        assertThat(userEntity.getId()).isEqualTo(ID);
        assertThat(userEntity.getUsername()).isEqualTo(USERNAME);
        assertThat(userEntity.getPassword()).isEqualTo(PASSWORD);
    }
}
