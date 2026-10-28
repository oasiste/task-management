package com.taskmanagement.auth.service;

import com.taskmanagement.auth.controller.AuthController.Credentials;
import com.taskmanagement.auth.controller.AuthController.Token;
import com.taskmanagement.auth.model.User;
import com.taskmanagement.auth.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository users;
    private final PasswordEncoder passwords;
    private final JwtEncoder jwt;
    private final R2dbcEntityTemplate template;

    public Mono<Void> register(Credentials credentials) {
        String email = normalizeEmail(credentials.email());

        return Mono.fromCallable(() -> passwords.encode(credentials.password()))
                .subscribeOn(Schedulers.boundedElastic())
                .map(hash -> new User(UUID.randomUUID(), email, hash))
                .flatMap(template::insert)
                .then()
                .onErrorMap(DuplicateKeyException.class,
                        e -> new ResponseStatusException(
                                HttpStatus.CONFLICT,
                                "Email already registered",
                                e
                        ));
    }

    public Mono<Token> login(Credentials credentials) {
        return users.findByEmail(normalizeEmail(credentials.email()))
                .filter(user -> passwords.matches(
                        credentials.password(),
                        user.getPasswordHash()
                ))
                .map(this::generateToken)
                .switchIfEmpty(Mono.error(
                        new ResponseStatusException(
                                HttpStatus.UNAUTHORIZED,
                                "Invalid credentials"
                        )
                ));
    }

    private Token generateToken(User user) {
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("task-auth")
                .subject(String.valueOf(user.getId()))
                .issuedAt(now)
                .expiresAt(now.plus(1, ChronoUnit.HOURS))
                .build();

        JwsHeader header = JwsHeader
                .with(SignatureAlgorithm.RS256)
                .build();

        String token = jwt.encode(
                JwtEncoderParameters.from(header, claims)
        ).getTokenValue();

        return new Token(token, "Bearer");
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase();
    }
}