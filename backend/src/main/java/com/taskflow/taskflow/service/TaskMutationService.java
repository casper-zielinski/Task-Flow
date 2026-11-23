package com.taskflow.taskflow.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import com.taskflow.taskflow.dto.Status;
import com.taskflow.taskflow.dto.TaskInput;
import com.taskflow.taskflow.dto.TaskResponse;
import com.taskflow.taskflow.dto.TaskUpdate;
import com.taskflow.taskflow.entity.StatusChangeEntity;
import com.taskflow.taskflow.entity.TaskEntity;
import com.taskflow.taskflow.exception.InvalidTaskException;
import com.taskflow.taskflow.exception.TaskNotFoundException;
import com.taskflow.taskflow.repository.TaskRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class TaskMutationService {

    private final TaskRepository taskRepository;

    public TaskMutationService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public TaskResponse createTask(TaskInput taskInput) {

        if (taskInput == null) {
            throw new InvalidTaskException("Task input must not be null");
        }
        if (taskInput.getTitle() == null || taskInput.getTitle().isBlank()) {
            throw new InvalidTaskException("Title must not be blank");
        }
        if (taskInput.getPriority() == null) {
            throw new InvalidTaskException("Priority must not be null");
        }

        LocalDate dueDate = parseDueDate(taskInput.getDueDate());

        Status status = taskInput.getStatus() != null ? taskInput.getStatus() : Status.OFFEN;

        TaskEntity newTask = TaskEntity.builder()
                .title(taskInput.getTitle())
                .description(taskInput.getDescription())
                .priority(taskInput.getPriority())
                .status(status)
                .dueDate(dueDate)
                .build();

        recordStatus(newTask, status, Instant.now());

        return TaskResponse.from(taskRepository.save(newTask));
    }

    @Transactional
    public TaskResponse updateTask(int id, TaskUpdate taskUpdate) {

        if (taskUpdate == null) {
            throw new InvalidTaskException("Task update input must not be null");
        }

        TaskEntity task = taskRepository.findById(id).orElseThrow(
                () -> new TaskNotFoundException("Task with ID: " + id + " not found")
        );

        if (taskUpdate.getTitle() == null || taskUpdate.getTitle().isBlank()) {
            throw new InvalidTaskException("Title must not be blank");
        }
        if (taskUpdate.getPriority() == null) {
            throw new InvalidTaskException("Priority must not be null");
        }
        if (taskUpdate.getStatus() == null) {
            throw new InvalidTaskException("Status must not be null");
        }
        LocalDate dueDate = parseDueDate(taskUpdate.getDueDate());

        task.setTitle(taskUpdate.getTitle());
        task.setDescription(taskUpdate.getDescription());
        task.setPriority(taskUpdate.getPriority());
        task.setDueDate(dueDate);

        if (task.getStatus() != taskUpdate.getStatus()) {
            recordStatus(task, taskUpdate.getStatus(), Instant.now());
        }

        return TaskResponse.from(taskRepository.save(task));
    }

    public void deleteTask(int id) {
        if (taskRepository.existsById(id)) {
            taskRepository.deleteById(id);
        } else {
            throw new TaskNotFoundException("Task with ID: " + id + " not found");
        }
    }

    private void recordStatus(TaskEntity task, Status status, Instant changedAt) {
        task.setStatus(status);
        task.setChangedAt(changedAt);
        task.getStatusHistory().add(StatusChangeEntity.builder()
                .task(task)
                .status(status)
                .changedAt(changedAt)
                .build());
    }

    private LocalDate parseDueDate(String dueDate) {
        if (dueDate == null) {
            return null;
        }
        try {
            return LocalDate.parse(dueDate);
        } catch (DateTimeParseException e) {
            throw new InvalidTaskException("Due date must be an ISO date (yyyy-MM-dd)");
        }
    }
}
