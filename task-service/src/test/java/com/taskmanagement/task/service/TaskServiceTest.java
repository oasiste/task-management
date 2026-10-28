package com.taskmanagement.task.service;

import com.taskmanagement.task.model.Task;
import com.taskmanagement.task.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository repository;

    @InjectMocks
    private TaskService taskService;

    private UUID userId;
    private UUID taskId;
    private Task task;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        taskId = UUID.randomUUID();

        // Adapt to your actual Task constructor
        task = new Task();
        task.setId(taskId);
        task.setUserId(userId);
        task.setTitle("Prepare interview");
        task.setStatus("TODO");
    }

    @Test
    void shouldReturnTaskWhenOwnedByUser() {
        when(repository.findByIdAndUserId(taskId, userId))
                .thenReturn(Mono.just(task));

        Mono<Task> result = taskService.get(taskId, userId);

        StepVerifier.create(result)
                .expectNextMatches(t ->
                        t.getId().equals(taskId) &&
                                t.getUserId().equals(userId))
                .verifyComplete();

        verify(repository).findByIdAndUserId(taskId, userId);
    }

    @Test
    void shouldReturnErrorWhenTaskDoesNotExist() {
        when(repository.findByIdAndUserId(taskId, userId))
                .thenReturn(Mono.empty());

        StepVerifier.create(taskService.get(taskId, userId))
                .expectError(ResponseStatusException.class)
                .verify();

        verify(repository).findByIdAndUserId(taskId, userId);
    }

    @Test
    void shouldNotReturnAnotherUsersTask() {
        UUID anotherUserId = UUID.randomUUID();

        when(repository.findByIdAndUserId(taskId, anotherUserId))
                .thenReturn(Mono.empty());

        StepVerifier.create(taskService.get(taskId, anotherUserId))
                .expectError(ResponseStatusException.class)
                .verify();

        verify(repository)
                .findByIdAndUserId(taskId, anotherUserId);

        verify(repository, never()).findById(taskId);
    }

    @Test
    void shouldListOnlyCurrentUsersTasks() {
        when(repository.findAllByUserId(userId))
                .thenReturn(Flux.just(task));

        StepVerifier.create(taskService.list(userId))
                .expectNext(task)
                .verifyComplete();

        verify(repository).findAllByUserId(userId);
    }

    @Test
    void shouldDeleteTaskOwnedByUser() {
        when(repository.findByIdAndUserId(taskId, userId))
                .thenReturn(Mono.just(task));

        when(repository.delete(task))
                .thenReturn(Mono.empty());

        StepVerifier.create(taskService.delete(taskId, userId))
                .verifyComplete();

        verify(repository).delete(task);
    }

    @Test
    void shouldNotDeleteTaskOwnedByAnotherUser() {
        UUID anotherUserId = UUID.randomUUID();

        when(repository.findByIdAndUserId(taskId, anotherUserId))
                .thenReturn(Mono.empty());

        StepVerifier.create(taskService.delete(taskId, anotherUserId))
                .expectError(ResponseStatusException.class)
                .verify();

        verify(repository, never()).delete(any(Task.class));
    }
}
