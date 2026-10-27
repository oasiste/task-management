package com.interview.task.model;
import java.time.LocalDate;
import java.util.UUID;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.*;


@Table("tasks")
public class Task {

    @Id
    private UUID id;

    @Column("user_id")
    private UUID userId;

    private String title;

    private String description;

    private String status;

    @Column("due_date")
    private LocalDate dueDate;

    public Task() {
    }

    public Task(UUID id, UUID userId, String title,
                String description, String status, LocalDate dueDate) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.status = status;
        this.dueDate = dueDate;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
}