package com.taskflow.taskflow.entity;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.taskflow.taskflow.dto.Priority;
import com.taskflow.taskflow.dto.Status;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tasks")
@Getter
@Setter
@NoArgsConstructor
public class TaskEntity {

      @Id
      @GeneratedValue(strategy = GenerationType.IDENTITY)
      private Integer id;

      @Column(nullable = false)
      private String title;

      @Column(columnDefinition = "TEXT")
      private String description;

      @Enumerated(EnumType.STRING)
      @Column(nullable = false)
      private Priority priority;

      @Enumerated(EnumType.STRING)
      @Column(nullable = false)
      private Status status;

      @Column(name = "due_date")
      private LocalDate dueDate;

      @CreationTimestamp
      @Column(name = "created_at", nullable = false, updatable = false)
      private Instant createdAt;

      /** Zeitpunkt der letzten Statusänderung. */
      @Column(name = "changed_at")
      private Instant changedAt;

      @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
      @OrderBy("changedAt ASC")
      private List<StatusChangeEntity> statusHistory = new ArrayList<>();
}
