package com.taskflow.taskflow.entity;

import java.time.Instant;

import com.taskflow.taskflow.dto.Status;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "status_changes")
@Getter
@Setter
@NoArgsConstructor
public class StatusChangeEntity {

      @Id
      @GeneratedValue(strategy = GenerationType.IDENTITY)
      private Long id;

      @ManyToOne(fetch = FetchType.LAZY, optional = false)
      @JoinColumn(name = "task_id", nullable = false)
      private TaskEntity task;

      @Enumerated(EnumType.STRING)
      @Column(nullable = false)
      private Status status;

      @Column(name = "changed_at", nullable = false)
      private Instant changedAt;
}
