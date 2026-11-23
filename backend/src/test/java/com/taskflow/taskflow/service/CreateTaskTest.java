package com.taskflow.taskflow.service;

import static com.taskflow.taskflow.service.UtilTest.INVALID_DUE_DATES;
import static com.taskflow.taskflow.service.UtilTest.existingTask;
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
import com.taskflow.taskflow.dto.TaskResponse;
import com.taskflow.taskflow.entity.StatusChangeEntity;
import com.taskflow.taskflow.entity.TaskEntity;
import com.taskflow.taskflow.exception.InvalidTaskException;
import com.taskflow.taskflow.repository.TaskRepository;

/**
 * POST /api/tasks: 201 on success, 400 on invalid input.
 * The repository is mocked, no Spring context and no database are involved.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CreateTaskTest {

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
      void validInput_savesTaskWithGivenValues() throws Exception {
            service.createTask(validInput());

            TaskEntity saved = savedEntity(repository);
            assertThat(saved.getTitle()).isEqualTo("Write test cases");
            assertThat(saved.getDescription()).isEqualTo("Cover all six endpoints");
            assertThat(saved.getPriority()).isEqualTo(Priority.HOCH);
            assertThat(saved.getStatus()).isEqualTo(Status.OFFEN);
            assertThat(saved.getDueDate()).isEqualTo(LocalDate.of(2026, 11, 15));
      }

      @Test
      void returnsTheSavedTask() throws Exception {
            TaskEntity fromDatabase = existingTask(Status.OFFEN);
            when(repository.save(any(TaskEntity.class))).thenReturn(fromDatabase);

            TaskResponse result = service.createTask(validInput());

            assertThat(result).isEqualTo(TaskResponse.from(fromDatabase));
      }

      @Test
      void createsInitialHistoryEntryWithStartStatus() throws Exception {
            TaskInput input = validInput();
            input.setStatus(Status.IN_BEARBEITUNG);

            service.createTask(input);

            TaskEntity saved = savedEntity(repository);
            assertThat(saved.getStatusHistory()).hasSize(1);
            StatusChangeEntity initial = saved.getStatusHistory().get(0);
            assertThat(initial.getStatus()).isEqualTo(Status.IN_BEARBEITUNG);
            assertThat(initial.getTask()).isSameAs(saved);
      }

      @Test
      void setsChangedAtToTheInitialHistoryEntry() throws Exception {
            Instant before = Instant.now();
            service.createTask(validInput());
            Instant after = Instant.now();

            TaskEntity saved = savedEntity(repository);
            assertThat(saved.getChangedAt()).isBetween(before, after);
            assertThat(saved.getStatusHistory().get(0).getChangedAt()).isEqualTo(saved.getChangedAt());
      }

      @Test
      void statusMissing_defaultsToOffen() throws Exception {
            TaskInput input = validInput();
            input.setStatus(null);

            service.createTask(input);

            assertThat(savedEntity(repository).getStatus()).isEqualTo(Status.OFFEN);
      }

      @Test
      void statusGiven_isKept() throws Exception {
            TaskInput input = validInput();
            input.setStatus(Status.IN_BEARBEITUNG);

            service.createTask(input);

            assertThat(savedEntity(repository).getStatus()).isEqualTo(Status.IN_BEARBEITUNG);
      }

      @Test
      void optionalFieldsMissing_areNull() throws Exception {
            TaskInput input = validInput();
            input.setDescription(null);
            input.setDueDate(null);

            service.createTask(input);

            TaskEntity saved = savedEntity(repository);
            assertThat(saved.getDescription()).isNull();
            assertThat(saved.getDueDate()).isNull();
      }

      @Test
      void doesNotSetIdOrCreatedAt() throws Exception {
            service.createTask(validInput());

            TaskEntity saved = savedEntity(repository);
            assertThat(saved.getId()).isNull();
            assertThat(saved.getCreatedAt()).isNull();
      }

      @Test
      void nullInput_throwsAndDoesNotSave() {
            assertThrows(Exception.class, () -> service.createTask(null));

            verify(repository, never()).save(any(TaskEntity.class));
      }

      @ParameterizedTest
      @NullAndEmptySource
      @ValueSource(strings = { " ", "   " })
      void titleBlank_throwsInvalidTask(String title) {
            TaskInput input = validInput();
            input.setTitle(title);

            assertThrows(InvalidTaskException.class, () -> service.createTask(input));

            verify(repository, never()).save(any(TaskEntity.class));
      }

      @Test
      void priorityMissing_throwsInvalidTask() {
            TaskInput input = validInput();
            input.setPriority(null);

            assertThrows(InvalidTaskException.class, () -> service.createTask(input));

            verify(repository, never()).save(any(TaskEntity.class));
      }

      @ParameterizedTest
      @MethodSource("invalidDueDates")
      void dueDateNotIsoDate_throwsInvalidTask(String dueDate) {
            TaskInput input = validInput();
            input.setDueDate(dueDate);

            assertThrows(InvalidTaskException.class, () -> service.createTask(input));

            verify(repository, never()).save(any(TaskEntity.class));
      }
}
