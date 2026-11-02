package com.taskflow.taskflow.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.mockito.ArgumentCaptor;

import com.taskflow.taskflow.dto.Priority;
import com.taskflow.taskflow.dto.Status;
import com.taskflow.taskflow.dto.TaskInput;
import com.taskflow.taskflow.entity.StatusChangeEntity;
import com.taskflow.taskflow.entity.TaskEntity;
import com.taskflow.taskflow.repository.TaskRepository;

/**
 * Shared helpers for the TaskMutationService tests (CreateTaskTest, UpdateTaskTest, DeleteTaskTest).
 */
final class UtilTest {

      static final int ID = 7;
      static final Instant CREATED_AT = Instant.parse("2026-10-01T08:00:00Z");
      static final Instant OLD_CHANGED_AT = Instant.parse("2026-10-02T09:00:00Z");

      static final String[] INVALID_DUE_DATES = { "15.11.2026", "abc", "2026-13-01", "2026-02-30",
                  "2026-11-15T10:00:00" };

      private UtilTest() {
      }

      static TaskInput validInput() {
            return new TaskInput("Write test cases", "Cover all six endpoints", Priority.HOCH,
                        Status.OFFEN, "2026-11-15");
      }

      static TaskEntity existingTask(Status status) {
            TaskEntity task = new TaskEntity();
            task.setId(ID);
            task.setTitle("Old title");
            task.setDescription("Old description");
            task.setPriority(Priority.NIEDRIG);
            task.setStatus(status);
            task.setDueDate(LocalDate.of(2026, 10, 20));
            task.setCreatedAt(CREATED_AT);
            task.setChangedAt(OLD_CHANGED_AT);

            StatusChangeEntity initial = new StatusChangeEntity();
            initial.setTask(task);
            initial.setStatus(status);
            initial.setChangedAt(OLD_CHANGED_AT);
            task.setStatusHistory(new ArrayList<>(List.of(initial)));
            return task;
      }

      static void givenSaveReturnsArgument(TaskRepository repository) {
            when(repository.save(any(TaskEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
      }

      static void givenExistingTask(TaskRepository repository, TaskEntity task) {
            when(repository.findById(ID)).thenReturn(Optional.of(task));
            when(repository.existsById(ID)).thenReturn(true);
      }

      static void givenNoTask(TaskRepository repository) {
            when(repository.findById(ID)).thenReturn(Optional.empty());
            when(repository.existsById(ID)).thenReturn(false);
      }

      static TaskEntity savedEntity(TaskRepository repository) {
            ArgumentCaptor<TaskEntity> captor = ArgumentCaptor.forClass(TaskEntity.class);
            verify(repository).save(captor.capture());
            return captor.getValue();
      }
}
