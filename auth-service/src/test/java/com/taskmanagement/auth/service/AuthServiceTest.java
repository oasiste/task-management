
package com.taskmanagement.auth.service;

import com.taskmanagement.auth.controller.AuthController.Credentials;
import com.taskmanagement.auth.model.User;
import com.taskmanagement.auth.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.server.ResponseStatusException;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository users;

    @Mock
    private PasswordEncoder passwords;

    @Mock
    private JwtEncoder jwt;

    @InjectMocks
    private AuthService authService;

    private Credentials credentials;
    private User user;

    @BeforeEach
    void setUp() {
        credentials = new Credentials(
                "test@interview.com",
                "password123"
        );

        user = new User(
                UUID.randomUUID(),
                "test@interview.com",
                "hashed-password"
        );
    }

    @Test
    void shouldRegisterUserSuccessfully() {


        when(passwords.encode("password123"))
                .thenReturn("hashed-password");

        when(users.save(any(User.class)))
                .thenAnswer(invocation ->
                        Mono.just(invocation.getArgument(0))
                );

        StepVerifier.create(authService.register(credentials))
                .verifyComplete();

        verify(users).save(argThat(saved ->
                saved.getId() != null &&
                        saved.getEmail().equals("test@interview.com") &&
                        saved.getPasswordHash().equals("hashed-password")
        ));

        verify(passwords).encode("password123");
    }

    @Test
    void shouldNormalizeEmailDuringRegistration() {
        Credentials input = new Credentials(
                "  TEST@INTERVIEW.COM  ",
                "password123"
        );

        when(passwords.encode("password123"))
                .thenReturn("hashed-password");

        when(users.save(any(User.class)))
                .thenAnswer(invocation ->
                        Mono.just(invocation.getArgument(0))
                );

        StepVerifier.create(authService.register(input))
                .verifyComplete();

        verify(users).save(argThat(saved ->
                saved.getEmail().equals("test@interview.com")
        ));
    }

    @Test
    void shouldHandleDuplicateKeyDuringRegistration() {

        when(passwords.encode("password123"))
                .thenReturn("hashed-password");

        when(users.save(any(User.class)))
                .thenReturn(Mono.error(
                        new DuplicateKeyException("Duplicate email")
                ));

        StepVerifier.create(authService.register(credentials))
                .expectErrorMatches(error ->
                        hasStatus(error, HttpStatus.CONFLICT)
                )
                .verify();
    }

    @Test
    void shouldLoginSuccessfully() {
        when(users.findByEmail("test@interview.com"))
                .thenReturn(Mono.just(user));

        when(passwords.matches(
                "password123",
                "hashed-password"
        )).thenReturn(true);

        when(jwt.encode(any(JwtEncoderParameters.class)))
                .thenReturn(mockJwt());

        StepVerifier.create(authService.login(credentials))
                .assertNext(token -> {
                    assertEquals("signed-jwt-token", token.accessToken());
                    assertEquals("Bearer", token.tokenType());
                })
                .verifyComplete();

        verify(jwt).encode(any(JwtEncoderParameters.class));
    }

    @Test
    void shouldRejectLoginWhenUserDoesNotExist() {
        when(users.findByEmail("test@interview.com"))
                .thenReturn(Mono.empty());

        StepVerifier.create(authService.login(credentials))
                .expectErrorMatches(error ->
                        hasStatus(error, HttpStatus.UNAUTHORIZED)
                )
                .verify();

        verifyNoInteractions(passwords, jwt);
    }

    @Test
    void shouldRejectLoginWhenPasswordIsIncorrect() {
        when(users.findByEmail("test@interview.com"))
                .thenReturn(Mono.just(user));

        when(passwords.matches(
                "wrong-password",
                "hashed-password"
        )).thenReturn(false);

        Credentials invalid = new Credentials(
                "test@interview.com",
                "wrong-password"
        );

        StepVerifier.create(authService.login(invalid))
                .expectErrorMatches(error ->
                        hasStatus(error, HttpStatus.UNAUTHORIZED)
                )
                .verify();

        verifyNoInteractions(jwt);
    }


    private Jwt mockJwt() {
        return Jwt.withTokenValue("signed-jwt-token")
                .header("alg", "RS256")
                .subject(String.valueOf(user.getId()))
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();
    }

    private boolean hasStatus(
            Throwable error,
            HttpStatus expectedStatus) {

        return error instanceof ResponseStatusException ex
                && ex.getStatusCode().value() == expectedStatus.value();
    }
}
