package com.example.TaskApp.repository;

import com.example.TaskApp.models.TaskDeadline;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TaskDeadlineRepository extends JpaRepository<TaskDeadline, Integer> {

    List<TaskDeadline> findByTaskId(int taskId);

    List<TaskDeadline> findByEscalatedFalseAndDueAtBefore(LocalDateTime time);
}
