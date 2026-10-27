package com.interview.auth.repository;
import java.util.UUID;

import com.interview.auth.model.User;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;
public interface UserRepository extends ReactiveCrudRepository<User, UUID> {
    Mono<User> findByEmail(String email);
}
