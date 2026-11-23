package com.taskflow.taskflow.service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import org.springframework.stereotype.Service;
import com.taskflow.taskflow.dto.Status;
import com.taskflow.taskflow.dto.TaskInput;
import com.taskflow.taskflow.entity.TaskEntity;
import com.taskflow.taskflow.exception.InvalidTaskException;
import com.taskflow.taskflow.repository.TaskRepository;

@Service
public class TaskMutationService {

    private final TaskRepository taskRepository;

    public TaskMutationService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public TaskEntity createTask(TaskInput taskInput) {

        if (taskInput == null) {
            throw new InvalidTaskException("Task input must not be null");
        }
        if (taskInput.getTitle() == null || taskInput.getTitle().isBlank()) {
            throw new InvalidTaskException("Title must not be blank");
        }
        if (taskInput.getPriority() == null) {
            throw new InvalidTaskException("Priority must not be null");
        }

        LocalDate dueDate = null;
        if (taskInput.getDueDate() != null) {
            try {
                dueDate = LocalDate.parse(taskInput.getDueDate());
            } catch (DateTimeParseException e) {
                throw new InvalidTaskException("Due date must be an ISO date (yyyy-MM-dd)");
            }
        }

        Status status = taskInput.getStatus() != null ? taskInput.getStatus() : Status.OFFEN;

        TaskEntity newTask = TaskEntity.builder()
                .title(taskInput.getTitle())
                .description(taskInput.getDescription())
                .priority(taskInput.getPriority())
                .status(status)
                .dueDate(dueDate)
                .build();

        return taskRepository.save(newTask);
    }

    public TaskEntity updateTask(int id, TaskInput taskInput) {
        return new TaskEntity();
    }

    public void deleteTask(int id) {
        taskRepository.deleteById(id);
    }
}
