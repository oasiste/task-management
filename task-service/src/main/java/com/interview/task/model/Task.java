package com.interview.task.model;
import java.time.LocalDate;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.*;


@Table("tasks")
@Data
@AllArgsConstructor
@NoArgsConstructor
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
}