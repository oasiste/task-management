package com.interview.auth.controller;

import com.interview.auth.service.AuthService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    public record Credentials(
            @Email @NotBlank String email,
            @NotBlank String password
    ) {}

    public record Token(
            String accessToken,
            String tokenType
    ) {}

    @PostMapping("/register")
    public Mono<ResponseEntity<Void>> register(
            @Valid @RequestBody Credentials credentials) {

        return authService.register(credentials)
                .thenReturn(ResponseEntity
                        .status(HttpStatus.CREATED)
                        .build());
    }

    @PostMapping("/login")
    public Mono<Token> login(
            @Valid @RequestBody Credentials credentials) {

        return authService.login(credentials);
    }
}