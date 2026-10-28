package com.taskmanagement.task.controller;

import com.taskmanagement.task.model.Task;
import com.taskmanagement.task.service.TaskService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    public record TaskInput(@NotBlank String title,
                            String description,
                            @NotBlank String status,
                            LocalDate dueDate) {}

    private UUID getUserId(Jwt jwt) {
        return UUID.fromString(jwt.getSubject());
    }

    @GetMapping
    public Flux<Task> list(@AuthenticationPrincipal Jwt jwt) {
        return taskService.list(getUserId(jwt));
    }

    @GetMapping("/{id}")
    public Mono<Task> get(@PathVariable UUID id,@AuthenticationPrincipal Jwt jwt) {
        return taskService.get(id, getUserId(jwt));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Task> create(
            @Valid @RequestBody TaskInput input,
            @AuthenticationPrincipal Jwt jwt) {
        return taskService.create(input, getUserId(jwt));
    }

    @PutMapping("/{id}")
    public Mono<Task> update(@PathVariable UUID id,@Valid @RequestBody TaskInput input,
                             @AuthenticationPrincipal Jwt jwt) {
        return taskService.update(id,input, getUserId(jwt));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable UUID id,@AuthenticationPrincipal Jwt jwt) {
        return taskService.delete(id, getUserId(jwt));
    }

}
