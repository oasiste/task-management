package com.taskmanagement.gateway;
import org.springframework.context.annotation.*;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
@Configuration
public class SecurityConfig {

    @Bean
    SecurityWebFilterChain chain(ServerHttpSecurity http) {
        return http.csrf(ServerHttpSecurity.CsrfSpec::disable)
          .authorizeExchange(e -> e.pathMatchers("/auth/**").permitAll()
            .anyExchange().authenticated())
          .oauth2ResourceServer(o -> o.jwt(j -> {})).build();
    }
}
