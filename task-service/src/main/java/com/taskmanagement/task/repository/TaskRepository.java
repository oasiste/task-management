package com.taskmanagement.task.repository;
import java.util.UUID;

import com.taskmanagement.task.model.Task;
import reactor.core.publisher.*;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
public interface TaskRepository extends ReactiveCrudRepository<Task,UUID> {
    Flux<Task> findAllByUserId(UUID ownerId);
    Mono<Task> findByIdAndUserId(UUID id, UUID ownerId);
}
