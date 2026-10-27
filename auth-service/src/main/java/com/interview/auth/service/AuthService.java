package com.interview.auth.service;

import com.interview.auth.controller.AuthController.Credentials;
import com.interview.auth.controller.AuthController.Token;
import com.interview.auth.model.User;
import com.interview.auth.repository.UserRepository;

import org.springframework.dao.DuplicateKeyException;
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
public class AuthService {

    private final UserRepository users;
    private final PasswordEncoder passwords;
    private final JwtEncoder jwt;

    public AuthService(
            UserRepository users,
            PasswordEncoder passwords,
            JwtEncoder jwt) {

        this.users = users;
        this.passwords = passwords;
        this.jwt = jwt;
    }

    public Mono<Void> register(Credentials credentials) {
        String email = normalizeEmail(credentials.email());

        return users.findByEmail(email)
                .flatMap(user -> Mono.<Void>error(
                        new ResponseStatusException(
                                HttpStatus.CONFLICT,
                                "Email already registered"
                        )
                ))
                .switchIfEmpty(Mono.defer(() ->
                        Mono.fromCallable(() ->
                                        passwords.encode(credentials.password())
                                )
                                .subscribeOn(Schedulers.boundedElastic())
                                .flatMap(hash -> {
                                    User user = new User(
                                            UUID.randomUUID(),
                                            email,
                                            hash
                                    );

                                    return users.save(user);
                                })
                                .then()
                ))
                .onErrorMap(
                        DuplicateKeyException.class,
                        e -> new ResponseStatusException(
                                HttpStatus.CONFLICT,
                                "Email already registered",
                                e
                        )
                );
    }

    public Mono<Token> login(Credentials credentials) {
        String email = normalizeEmail(credentials.email());

        return users.findByEmail(email)
                .flatMap(user ->
                        Mono.fromCallable(() ->
                                        passwords.matches(
                                                credentials.password(),
                                                user.getPasswordHash()
                                        )
                                )
                                .subscribeOn(Schedulers.boundedElastic())
                                .filter(Boolean::booleanValue)
                                .map(valid -> generateToken(user))
                )
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