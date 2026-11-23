package com.taskflow.taskflow.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request body of PUT /tasks/{id}: full replacement of a task.
 * Required: title, priority, status. Optional (cleared if omitted): description, dueDate.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskUpdate {
      private String title;
      private String description;
      private Priority priority;
      private Status status;
      private String dueDate;
}
