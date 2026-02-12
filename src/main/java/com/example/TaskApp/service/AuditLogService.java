package com.example.TaskApp.service;

import com.example.TaskApp.models.AuditActionType;
import com.example.TaskApp.models.AuditLog;
import com.example.TaskApp.models.TaskState;
import com.example.TaskApp.repository.AuditLogRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void logTaskCreated(int taskId, int actorUserId) {
        save(taskId, actorUserId, AuditActionType.TASK_CREATED, null, null,
                "Task created");
    }

    public void logTaskStateChange(
            int taskId,
            int actorUserId,
            TaskState oldState,
            TaskState newState) {
        save(taskId, actorUserId, AuditActionType.TASK_STATE_CHANGED,
                oldState, newState,
                "State changed from " + oldState + " to " + newState);
    }

    public void logDependencyAdded(int taskId, int actorUserId, int blockerTaskId) {
        save(taskId, actorUserId, AuditActionType.DEPENDENCY_ADDED,
                null, null,
                "Blocked by task " + blockerTaskId);
    }

    public void logDependencyRemoved(int taskId, int actorUserId, int blockerTaskId) {
        save(taskId, actorUserId, AuditActionType.DEPENDENCY_REMOVED,
                null, null,
                "Unblocked from task " + blockerTaskId);
    }

    public void logTaskOverdue(int taskId, int actorUserId) {
        save(taskId, actorUserId, AuditActionType.TASK_OVERDUE,
                null, null,
                "Task overdue");
    }

    public void logTaskEscalated(int taskId, int escalatedToUserId) {
        save(taskId, escalatedToUserId, AuditActionType.TASK_ESCALATED,
                null, null,
                "Task escalated");
    }

    public java.util.List<AuditLog> getLogsForTask(int taskId) {
        return auditLogRepository.findByTaskId(taskId);
    }

    private void save(
            Integer taskId,
            int actorUserId,
            AuditActionType type,
            TaskState previous,
            TaskState next,
            String comment) {
        AuditLog log = new AuditLog();
        log.setTaskId(taskId);
        log.setActorUserId(actorUserId);
        log.setActionType(type);
        log.setPreviousState(previous);
        log.setNewState(next);
        log.setComment(comment);
        log.setCreatedAt(LocalDateTime.now());

        auditLogRepository.save(log);
    }
}
