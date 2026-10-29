package com.taskflow.taskflow.service;

import org.springframework.stereotype.Service;
import com.taskflow.taskflow.dto.TaskInput;
import com.taskflow.taskflow.entity.TaskEntity;

@Service 
public class TaskMutationService {
      public TaskEntity createTask(TaskInput taskInput) {
            return new TaskEntity();
      }

      public TaskEntity updateTask(TaskInput taskInput) {
            return new TaskEntity();
      }

      public void deleteTask(int id) {
            
      }
}
