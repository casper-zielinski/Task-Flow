package com.taskflow.taskflow.service;

import com.taskflow.taskflow.dto.TaskDetailResponse;
import com.taskflow.taskflow.dto.TaskResponse;
import com.taskflow.taskflow.dto.TaskStatsResponse;
import com.taskflow.taskflow.dto.Priority;
import com.taskflow.taskflow.dto.Status;
import com.taskflow.taskflow.entity.TaskEntity;
import com.taskflow.taskflow.repository.TaskRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Read-only use cases for tasks (GET endpoints).
 */
@Service
public class TaskQueryService {

    /** Priority HOCH first, then due date (earliest first, tasks without due date last). */
    private static final Comparator<TaskEntity> DEFAULT_ORDER = Comparator
            .comparing(TaskEntity::getPriority, Comparator.reverseOrder())
            .thenComparing(TaskEntity::getDueDate, Comparator.nullsLast(Comparator.naturalOrder()));

    private final TaskRepository taskRepository;

    /**
     * Creates the service.
     *
     * @param taskRepository repository for tasks
     */
    public TaskQueryService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    /**
     * Returns all tasks that match the given filters, sorted by priority and then by due date.
     * Every filter is optional; {@code null} means no restriction.
     *
     * @param status   only tasks with this status
     * @param priority only tasks with this priority
     * @param due      only tasks matching this due date filter: {@code overdue}, {@code today} or {@code thisWeek}
     * @param search   only tasks whose title or description contains this text (case-insensitive)
     * @return the matching tasks
     * @throws ResponseStatusException with status 400 if {@code due} has an unknown value
     */
    public List<TaskResponse> getTasks(Status status, Priority priority, String due, String search) {
        LocalDate today = LocalDate.now();
        return taskRepository.findAll().stream()
                .filter(task -> status == null || task.getStatus() == status)
                .filter(task -> priority == null || task.getPriority() == priority)
                .filter(task -> due == null || matchesDue(task, due, today))
                .filter(task -> search == null || matchesSearch(task, search))
                .sorted(DEFAULT_ORDER)
                .map(TaskResponse::from)
                .toList();
    }

    /**
     * Returns a single task including its status history.
     *
     * @param id ID of the task
     * @return the task with its status history
     * @throws ResponseStatusException with status 404 if no task with this ID exists
     */
    public TaskDetailResponse getTaskById(Integer id) {
        TaskEntity task = taskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Task with ID " + id + " was not found."));
        return TaskDetailResponse.from(task);
    }

    /**
     * Returns the number of tasks per status and the number of overdue tasks.
     * A task is overdue if its due date is before today and it is not ABGESCHLOSSEN.
     *
     * @return the statistics
     */
    public TaskStatsResponse getTaskStats() {
        List<TaskEntity> tasks = taskRepository.findAll();
        Map<Status, Long> byStatus = new LinkedHashMap<>();
        for (Status status : Status.values()) {
            byStatus.put(status, tasks.stream().filter(task -> task.getStatus() == status).count());
        }
        LocalDate today = LocalDate.now();
        long overdue = tasks.stream().filter(task -> matchesDue(task, "overdue", today)).count();
        return new TaskStatsResponse(byStatus, overdue);
    }

    private static boolean matchesDue(TaskEntity task, String due, LocalDate today) {
        LocalDate dueDate = task.getDueDate();
        switch (due) {
            case "overdue":
                return dueDate != null && dueDate.isBefore(today) && task.getStatus() != Status.ABGESCHLOSSEN;
            case "today":
                return today.equals(dueDate);
            case "thisWeek":
                LocalDate monday = today.with(DayOfWeek.MONDAY);
                LocalDate sunday = today.with(DayOfWeek.SUNDAY);
                return dueDate != null && !dueDate.isBefore(monday) && !dueDate.isAfter(sunday);
            default:
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid value '" + due
                        + "' for parameter 'due'. Allowed values: overdue, today, thisWeek.");
        }
    }

    private static boolean matchesSearch(TaskEntity task, String search) {
        String term = search.toLowerCase();
        return contains(task.getTitle(), term) || contains(task.getDescription(), term);
    }

    private static boolean contains(String text, String term) {
        return text != null && text.toLowerCase().contains(term);
    }
}
