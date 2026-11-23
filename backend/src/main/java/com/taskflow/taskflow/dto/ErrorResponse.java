package com.taskflow.taskflow.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Error body as defined by the "Error" schema in openapi.yaml.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
      private int status;
      private String message;
}
