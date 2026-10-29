package com.taskflow.taskflow.service;

import com.taskflow.taskflow.dto.TaskDetailResponse;
import com.taskflow.taskflow.dto.TaskResponse;
import com.taskflow.taskflow.dto.TaskStatsResponse;
import com.taskflow.taskflow.dto.Priority;
import com.taskflow.taskflow.dto.Status;
import com.taskflow.taskflow.entity.TaskEntity;
import com.taskflow.taskflow.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link TaskQueryService}. The repository is mocked, so no database is needed.
 */
@ExtendWith(MockitoExtension.class)
class TaskQueryServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskQueryService taskQueryService;

    /**
     * Without filters all tasks are returned.
     */
    @Test
    void getTasksReturnsAllTasks() {
        when(taskRepository.findAll()).thenReturn(List.of(
                createTask(1, "Write test cases"),
                createTask(2, "Fix login bug")));

        List<TaskResponse> result = taskQueryService.getTasks(null, null, null, null);

        assertEquals(2, result.size());
    }

    /**
     * An existing task is returned with its data.
     */
    @Test
    void getTaskByIdReturnsTask() {
        when(taskRepository.findById(1)).thenReturn(Optional.of(createTask(1, "Write test cases")));

        TaskDetailResponse result = taskQueryService.getTaskById(1);

        assertEquals(1, result.id());
        assertEquals("Write test cases", result.title());
    }

    /**
     * Requesting a task that does not exist throws an exception (results in status 404).
     */
    @Test
    void getTaskByIdThrowsWhenTaskDoesNotExist() {
        when(taskRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> taskQueryService.getTaskById(999));
    }

    /**
     * The statistics contain the number of tasks per status and the number of overdue tasks.
     */
    @Test
    void getTaskStatsReturnsCounts() {
        TaskEntity open = createTask(1, "Write test cases");
        TaskEntity overdue = createTask(2, "Fix login bug");
        overdue.setDueDate(LocalDate.now().minusDays(1));
        TaskEntity done = createTask(3, "Old finished task");
        done.setStatus(Status.ABGESCHLOSSEN);
        when(taskRepository.findAll()).thenReturn(List.of(open, overdue, done));

        TaskStatsResponse result = taskQueryService.getTaskStats();

        assertEquals(2L, result.byStatus().get(Status.OFFEN));
        assertEquals(0L, result.byStatus().get(Status.IN_BEARBEITUNG));
        assertEquals(1L, result.byStatus().get(Status.ABGESCHLOSSEN));
        assertEquals(1L, result.overdue());
    }

    /**
     * Creates a simple task for a test.
     *
     * @param id    ID of the task
     * @param title title of the task
     * @return the task
     */
    private static TaskEntity createTask(Integer id, String title) {
        TaskEntity task = new TaskEntity();
        task.setId(id);
        task.setTitle(title);
        task.setPriority(Priority.MITTEL);
        task.setStatus(Status.OFFEN);
        task.setDueDate(LocalDate.now());
        return task;
    }
}
