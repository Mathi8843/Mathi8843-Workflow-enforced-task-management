package com.example.TaskApp.service;

import com.example.TaskApp.models.Task;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EscalationService {

    private static final int SYSTEM_ACTOR_ID = 0;

    private final AuditLogService auditLogService;
    private final TaskService taskService;

    public EscalationService(
            AuditLogService auditLogService,
            TaskService taskService
    ) {
        this.auditLogService = auditLogService;
        this.taskService = taskService;
    }

    @Transactional
    public void handleOverdueTask(int taskId) {

        Task task = taskService.findTaskOrThrow(taskId);

        auditLogService.logTaskOverdue(taskId, SYSTEM_ACTOR_ID);
        auditLogService.logTaskEscalated(taskId, task.getAssigneeId());
    }
}
