package com.taskflow.taskflow.controller.mutations;

import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
public class TaskController {

      private List<String> tasks = new ArrayList<>(); //placeholder for DB and Service

      public TaskController() {
      }

      @PostMapping()
      public List<String> newTask(@RequestBody String taskdescription) {
            tasks.add(taskdescription);
            return tasks;
      }

}
