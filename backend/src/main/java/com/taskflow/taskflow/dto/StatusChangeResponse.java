package com.taskflow.taskflow.dto;

import com.taskflow.taskflow.entity.StatusChangeEntity;

import java.time.Instant;

/**
 * One entry of the status history ({@code StatusChange} schema in openapi.yaml).
 */
public record StatusChangeResponse(Status status, Instant changedAt) {

    /**
     * Creates the response from a status change entity.
     *
     * @param change the entity
     * @return the response
     */
    public static StatusChangeResponse from(StatusChangeEntity change) {
        return new StatusChangeResponse(change.getStatus(), change.getChangedAt());
    }
}
