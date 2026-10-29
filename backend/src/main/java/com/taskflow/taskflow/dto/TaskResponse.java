package com.taskflow.taskflow.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.taskflow.taskflow.entity.TaskEntity;

import java.time.Instant;
import java.time.LocalDate;

/**
 * A task in the list view ({@code Task} schema in openapi.yaml).
 * Optional fields that are not set are left out of the JSON.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record TaskResponse(
        Integer id,
        String title,
        String description,
        Priority priority,
        Status status,
        LocalDate dueDate,
        Instant createdAt,
        Instant changedAt) {

    /**
     * Creates the response from a task entity.
     *
     * @param task the entity
     * @return the response
     */
    public static TaskResponse from(TaskEntity task) {
        return new TaskResponse(task.getId(), task.getTitle(), task.getDescription(), task.getPriority(),
                task.getStatus(), task.getDueDate(), task.getCreatedAt(), task.getChangedAt());
    }
}
