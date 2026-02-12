package com.example.TaskApp.repository;

import com.example.TaskApp.models.TaskDependency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskDependencyRepository extends JpaRepository<TaskDependency, Integer> {

    List<TaskDependency> findByBlockedTaskId(int blockedTaskId);

    boolean existsByBlockerTaskIdAndBlockedTaskId(int blockerTaskId, int blockedTaskId);
}
