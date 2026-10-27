package com.interview.task;
import org.springframework.context.annotation.*;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
@Configuration
public class TaskSecurity {

    @Bean
    SecurityWebFilterChain chain(ServerHttpSecurity http) {
        return http.csrf(ServerHttpSecurity.CsrfSpec::disable)
          .authorizeExchange(e -> e.anyExchange().authenticated())
          .oauth2ResourceServer(o -> o.jwt(j -> {})).build();
    }
}
