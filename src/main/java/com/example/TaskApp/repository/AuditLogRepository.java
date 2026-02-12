package com.example.TaskApp.repository;

import com.example.TaskApp.models.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLog, Integer> {
    List<AuditLog> findByTaskId(int taskId);
    List<AuditLog> findByActorUserId(int actorUserId);
}
