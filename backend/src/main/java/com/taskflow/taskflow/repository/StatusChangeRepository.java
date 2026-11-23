package com.taskflow.taskflow.repository;

import com.taskflow.taskflow.entity.StatusChangeEntity;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatusChangeRepository extends JpaRepository<@NonNull StatusChangeEntity,@NonNull Integer> {
}
