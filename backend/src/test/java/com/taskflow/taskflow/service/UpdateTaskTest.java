package com.taskflow.taskflow.service;

import static com.taskflow.taskflow.service.UtilTest.CREATED_AT;
import static com.taskflow.taskflow.service.UtilTest.ID;
import static com.taskflow.taskflow.service.UtilTest.INVALID_DUE_DATES;
import static com.taskflow.taskflow.service.UtilTest.OLD_CHANGED_AT;
import static com.taskflow.taskflow.service.UtilTest.existingTask;
import static com.taskflow.taskflow.service.UtilTest.givenExistingTask;
import static com.taskflow.taskflow.service.UtilTest.givenNoTask;
import static com.taskflow.taskflow.service.UtilTest.givenSaveReturnsArgument;
import static com.taskflow.taskflow.service.UtilTest.savedEntity;
import static com.taskflow.taskflow.service.UtilTest.validInput;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.taskflow.taskflow.dto.Priority;
import com.taskflow.taskflow.dto.Status;
import com.taskflow.taskflow.dto.TaskInput;
import com.taskflow.taskflow.entity.StatusChangeEntity;
import com.taskflow.taskflow.entity.TaskEntity;
import com.taskflow.taskflow.exception.InvalidTaskException;
import com.taskflow.taskflow.exception.TaskNotFoundException;
import com.taskflow.taskflow.repository.TaskRepository;

/**
 * PUT /api/tasks/{id}: 200 on success, 400 on invalid input, 404 if the task does not exist.
 * The repository is mocked, no Spring context and no database are involved.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UpdateTaskTest {

      @Mock
      private TaskRepository repository;

      @InjectMocks
      private TaskMutationService service;

      @BeforeEach
      void setUp() {
            givenSaveReturnsArgument(repository);
      }

      static String[] invalidDueDates() {
            return INVALID_DUE_DATES;
      }

      @Test
      void unknownId_throwsNotFoundAndDoesNotSave() {
            givenNoTask(repository);

            assertThrows(TaskNotFoundException.class, () -> service.updateTask(ID, validInput()));

            verify(repository, never()).save(any(TaskEntity.class));
      }

      @Test
      void validInput_replacesAllAttributes() {
            givenExistingTask(repository, existingTask(Status.OFFEN));
            TaskInput input = new TaskInput("New title", "New description", Priority.HOCH,
                        Status.IN_BEARBEITUNG, "2026-12-24");

            service.updateTask(ID, input);

            TaskEntity saved = savedEntity(repository);
            assertThat(saved.getTitle()).isEqualTo("New title");
            assertThat(saved.getDescription()).isEqualTo("New description");
            assertThat(saved.getPriority()).isEqualTo(Priority.HOCH);
            assertThat(saved.getStatus()).isEqualTo(Status.IN_BEARBEITUNG);
            assertThat(saved.getDueDate()).isEqualTo(LocalDate.of(2026, 12, 24));
      }

      @Test
      void returnsTheSavedTask() {
            givenExistingTask(repository, existingTask(Status.OFFEN));
            TaskEntity fromDatabase = existingTask(Status.OFFEN);
            when(repository.save(any(TaskEntity.class))).thenReturn(fromDatabase);

            TaskEntity result = service.updateTask(ID, validInput());

            assertThat(result).isSameAs(fromDatabase);
      }

      @Test
      void keepsIdAndCreatedAt() {
            givenExistingTask(repository, existingTask(Status.OFFEN));

            service.updateTask(ID, validInput());

            TaskEntity saved = savedEntity(repository);
            assertThat(saved.getId()).isEqualTo(ID);
            assertThat(saved.getCreatedAt()).isEqualTo(CREATED_AT);
      }

      @Test
      void optionalFieldsOmitted_areCleared() {
            givenExistingTask(repository, existingTask(Status.OFFEN));
            TaskInput input = validInput();
            input.setDescription(null);
            input.setDueDate(null);

            service.updateTask(ID, input);

            TaskEntity saved = savedEntity(repository);
            assertThat(saved.getDescription()).isNull();
            assertThat(saved.getDueDate()).isNull();
      }

      @Test
      void statusChanged_addsHistoryEntryAndUpdatesChangedAt() {
            givenExistingTask(repository, existingTask(Status.OFFEN));
            TaskInput input = validInput();
            input.setStatus(Status.IN_BEARBEITUNG);

            Instant before = Instant.now();
            service.updateTask(ID, input);
            Instant after = Instant.now();

            TaskEntity saved = savedEntity(repository);
            assertThat(saved.getStatusHistory()).hasSize(2);
            StatusChangeEntity latest = saved.getStatusHistory().get(1);
            assertThat(latest.getStatus()).isEqualTo(Status.IN_BEARBEITUNG);
            assertThat(latest.getChangedAt()).isBetween(before, after);
            assertThat(saved.getChangedAt()).isBetween(before, after);
      }

      @Test
      void statusChanged_keepsExistingHistoryOldestFirst() {
            givenExistingTask(repository, existingTask(Status.OFFEN));
            TaskInput input = validInput();
            input.setStatus(Status.ABGESCHLOSSEN);

            service.updateTask(ID, input);

            List<StatusChangeEntity> history = savedEntity(repository).getStatusHistory();
            assertThat(history.get(0).getStatus()).isEqualTo(Status.OFFEN);
            assertThat(history.get(0).getChangedAt()).isEqualTo(OLD_CHANGED_AT);
            assertThat(history.get(1).getStatus()).isEqualTo(Status.ABGESCHLOSSEN);
            assertThat(history.get(1).getChangedAt()).isAfterOrEqualTo(history.get(0).getChangedAt());
      }

      @Test
      void statusUnchanged_doesNotTouchHistoryOrChangedAt() {
            givenExistingTask(repository, existingTask(Status.IN_BEARBEITUNG));
            TaskInput input = validInput();
            input.setStatus(Status.IN_BEARBEITUNG);

            service.updateTask(ID, input);

            TaskEntity saved = savedEntity(repository);
            assertThat(saved.getStatusHistory()).hasSize(1);
            assertThat(saved.getChangedAt()).isEqualTo(OLD_CHANGED_AT);
      }

      @Test
      void nullInput_throwsAndDoesNotSave() {
            givenExistingTask(repository, existingTask(Status.OFFEN));

            assertThrows(Exception.class, () -> service.updateTask(ID, null));

            verify(repository, never()).save(any(TaskEntity.class));
      }

      @ParameterizedTest
      @NullAndEmptySource
      @ValueSource(strings = { " ", "   " })
      void titleBlank_throwsInvalidTask(String title) {
            givenExistingTask(repository, existingTask(Status.OFFEN));
            TaskInput input = validInput();
            input.setTitle(title);

            assertThrows(InvalidTaskException.class, () -> service.updateTask(ID, input));

            verify(repository, never()).save(any(TaskEntity.class));
      }

      @Test
      void priorityMissing_throwsInvalidTask() {
            givenExistingTask(repository, existingTask(Status.OFFEN));
            TaskInput input = validInput();
            input.setPriority(null);

            assertThrows(InvalidTaskException.class, () -> service.updateTask(ID, input));

            verify(repository, never()).save(any(TaskEntity.class));
      }

      @Test
      void statusMissing_throwsInvalidTask() {
            // unlike POST, the status is required for PUT so it is never reset by accident
            givenExistingTask(repository, existingTask(Status.IN_BEARBEITUNG));
            TaskInput input = validInput();
            input.setStatus(null);

            assertThrows(InvalidTaskException.class, () -> service.updateTask(ID, input));

            verify(repository, never()).save(any(TaskEntity.class));
      }

      @ParameterizedTest
      @MethodSource("invalidDueDates")
      void dueDateNotIsoDate_throwsInvalidTask(String dueDate) {
            givenExistingTask(repository, existingTask(Status.OFFEN));
            TaskInput input = validInput();
            input.setDueDate(dueDate);

            assertThrows(InvalidTaskException.class, () -> service.updateTask(ID, input));

            verify(repository, never()).save(any(TaskEntity.class));
      }
}
