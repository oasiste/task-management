package com.interview.task.service;

import com.interview.task.controller.TaskController;
import com.interview.task.model.Task;
import com.interview.task.repository.TaskRepository;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class TaskService {

    private final TaskRepository repository;
    private final R2dbcEntityTemplate template;

    public TaskService(
            TaskRepository repository,
            R2dbcEntityTemplate template) {
        this.repository = repository;
        this.template = template;
    }

    public Flux<Task> list(UUID userId) {
        return repository.findAllByUserId(userId);
    }

    public Mono<Task> get(UUID id, UUID userId) {
        return repository
                .findByIdAndUserId(id,userId)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND,"Task not found")));
    }

    public Mono<Task> create(TaskController.TaskInput input, UUID userId)
    {
        Task task = new Task(
                UUID.randomUUID(),
                userId,
                input.title(),
                input.description(),
                input.status(),
                input.dueDate()
        );

        return template.insert(Task.class).using(task);
    }

    public Mono<Task> update(UUID id,TaskController.TaskInput input, UUID userId) {
        return get(id,userId)
                .flatMap(existing -> repository.save(new Task(id,userId,input.title(),
                        input.description(),input.status(),input.dueDate())));
    }

    public Mono<Void> delete(UUID id,UUID userId) {
        return get(id,userId).flatMap(repository::delete);
    }
}
