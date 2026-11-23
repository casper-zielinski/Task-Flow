package com.taskflow.taskflow.controller.queries;

import com.taskflow.taskflow.dto.TaskDetailResponse;
import com.taskflow.taskflow.dto.TaskResponse;
import com.taskflow.taskflow.dto.TaskStatsResponse;
import com.taskflow.taskflow.dto.Priority;
import com.taskflow.taskflow.dto.Status;
import com.taskflow.taskflow.service.TaskQueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Read-only endpoints for tasks (GET). See openapi.yaml for the contract.
 */
@RestController
@RequestMapping("/tasks")
public class TaskQueryController {

    private final TaskQueryService taskQueryService;

    /**
     * Creates the controller.
     *
     * @param taskQueryService service for task queries
     */
    public TaskQueryController(TaskQueryService taskQueryService) {
        this.taskQueryService = taskQueryService;
    }

    /**
     * {@code GET /api/tasks}: returns the tasks, sorted by priority and then by due date.
     *
     * @param status   optional status filter
     * @param priority optional priority filter
     * @param due      optional due date filter ({@code overdue}, {@code today}, {@code thisWeek})
     * @param search   optional search term for title and description
     * @return the matching tasks
     */
    @GetMapping
    public List<TaskResponse> getTasks(
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) Priority priority,
            @RequestParam(required = false) String due,
            @RequestParam(required = false) String search) {
        return taskQueryService.getTasks(status, priority, due, search);
    }

    /**
     * {@code GET /api/tasks/stats}: returns the number of tasks per status and the number of overdue tasks.
     *
     * @return the statistics
     */
    @GetMapping("/stats")
    public TaskStatsResponse getTaskStats() {
        return taskQueryService.getTaskStats();
    }

    /**
     * {@code GET /api/tasks/{id}}: returns a single task including its status history.
     *
     * @param id ID of the task
     * @return the task
     */
    @GetMapping("/{id}")
    public TaskDetailResponse getTaskById(@PathVariable Integer id) {
        return taskQueryService.getTaskById(id);
    }
}
