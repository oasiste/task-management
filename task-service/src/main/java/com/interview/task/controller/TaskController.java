package com.interview.task.controller;

import com.interview.task.model.Task;
import com.interview.task.service.TaskService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    public record TaskInput(@NotBlank String title, String description,
                            @NotBlank String status,
                            LocalDate dueDate) {}

    private UUID owner(Jwt jwt) {
        return UUID.fromString(jwt.getSubject());
    }

    @GetMapping
    public Flux<Task> list(@AuthenticationPrincipal Jwt jwt) {
        return taskService.list(owner(jwt));
    }

    @GetMapping("/{id}")
    public Mono<Task> get(@PathVariable UUID id,@AuthenticationPrincipal Jwt jwt) {
        return taskService.get(id,owner(jwt));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Task> create(
            @Valid @RequestBody TaskInput input,
            @AuthenticationPrincipal Jwt jwt) {
        return taskService.create(input,owner(jwt));
    }

    @PutMapping("/{id}")
    public Mono<Task> update(@PathVariable UUID id,@Valid @RequestBody TaskInput input,
                             @AuthenticationPrincipal Jwt jwt) {
        return taskService.update(id,input,owner(jwt));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable UUID id,@AuthenticationPrincipal Jwt jwt) {
        return taskService.delete(id,owner(jwt));
    }

}
