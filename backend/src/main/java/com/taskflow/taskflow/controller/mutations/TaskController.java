package com.taskflow.taskflow.controller.mutations;

import com.taskflow.taskflow.dto.ErrorResponse;
import com.taskflow.taskflow.dto.TaskInput;
import com.taskflow.taskflow.exception.InvalidTaskException;
import com.taskflow.taskflow.service.TaskMutationService;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskMutationService taskMutationService;

    public TaskController(TaskMutationService taskMutationService) {
        this.taskMutationService = taskMutationService;
    }

    @PostMapping()
    public ResponseEntity<?> newTask(@RequestBody TaskInput taskInput) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(taskMutationService.createTask(taskInput));
        } catch (InvalidTaskException e) {
            return error(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            return error(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error. Please try again later.");
        }
    }

    private ResponseEntity<@NonNull ErrorResponse> error(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(new ErrorResponse(status.value(), message));
    }

}
