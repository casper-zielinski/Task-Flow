package com.taskflow.taskflow.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.taskflow.taskflow.entity.TaskEntity;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

/**
 * A task including its status history ({@code TaskDetail} schema in openapi.yaml).
 * Optional fields that are not set are left out of the JSON.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record TaskDetailResponse(
        Integer id,
        String title,
        String description,
        Priority priority,
        Status status,
        LocalDate dueDate,
        Instant createdAt,
        Instant changedAt,
        List<StatusChangeResponse> statusHistory) {

    /**
     * Creates the response from a task entity, including its status history.
     *
     * @param task the entity
     * @return the response
     */
    public static TaskDetailResponse from(TaskEntity task) {
        List<StatusChangeResponse> history = task.getStatusHistory().stream()
                .map(StatusChangeResponse::from)
                .toList();
        return new TaskDetailResponse(task.getId(), task.getTitle(), task.getDescription(), task.getPriority(),
                task.getStatus(), task.getDueDate(), task.getCreatedAt(), task.getChangedAt(), history);
    }
}
