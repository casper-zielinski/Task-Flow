package com.taskflow.taskflow.service;

import static com.taskflow.taskflow.service.UtilTest.ID;
import static com.taskflow.taskflow.service.UtilTest.existingTask;
import static com.taskflow.taskflow.service.UtilTest.givenExistingTask;
import static com.taskflow.taskflow.service.UtilTest.givenNoTask;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.taskflow.taskflow.dto.Status;
import com.taskflow.taskflow.entity.TaskEntity;
import com.taskflow.taskflow.exception.TaskNotFoundException;
import com.taskflow.taskflow.repository.TaskRepository;

/**
 * DELETE /api/tasks/{id}: 204 on success, 404 if the task does not exist.
 * The repository is mocked, no Spring context and no database are involved.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DeleteTaskTest {

      @Mock
      private TaskRepository repository;

      @InjectMocks
      private TaskMutationService service;

      @Test
      void existingId_deletesTaskOnce() {
            givenExistingTask(repository, existingTask(Status.OFFEN));

            service.deleteTask(ID);

            verify(repository, times(1)).deleteById(ID);
      }

      @Test
      void unknownId_throwsNotFoundAndDeletesNothing() {
            givenNoTask(repository);

            assertThrows(TaskNotFoundException.class, () -> service.deleteTask(ID));

            verify(repository, never()).deleteById(any());
            verify(repository, never()).delete(any(TaskEntity.class));
      }
}
