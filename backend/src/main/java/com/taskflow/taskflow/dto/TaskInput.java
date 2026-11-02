package com.taskflow.taskflow.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskInput {
      private String title;
      private String description;
      private Priority priority;
      private Status status;
      private String dueDate;
}
