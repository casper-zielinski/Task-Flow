package com.taskflow.taskflow.dto;


import java.util.Map;

/**
 * Task statistics ({@code TaskStats} schema in openapi.yaml).
 *
 * @param byStatus number of tasks per status
 * @param overdue  number of overdue tasks
 */
public record TaskStatsResponse(Map<Status, Long> byStatus, long overdue) {
}
