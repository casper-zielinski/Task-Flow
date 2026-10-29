package com.taskflow.taskflow.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.taskflow.taskflow.entity.TaskEntity;

public interface TaskRepository extends JpaRepository<TaskEntity, Integer> {

}
