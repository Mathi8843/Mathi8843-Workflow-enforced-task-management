package com.example.TaskApp.service;

import com.example.TaskApp.models.*;
import com.example.TaskApp.repository.TaskDeadlineRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DeadlineService {

    private final TaskDeadlineRepository deadlineRepository;
    private final TaskService taskService;
    private final EscalationService escalationService;
    private final AuditLogService auditLogService;

    public DeadlineService(
            TaskDeadlineRepository deadlineRepository,
            TaskService taskService,
            EscalationService escalationService,
            AuditLogService auditLogService
    ) {
        this.deadlineRepository = deadlineRepository;
        this.taskService = taskService;
        this.escalationService = escalationService;
        this.auditLogService = auditLogService;
    }

    /* ============ DEADLINE CREATION ============ */

    @Transactional
    public TaskDeadline addDeadline(int actorUserId, int taskId,
                                    DeadlineType type, LocalDateTime dueAt) {

        taskService.findTaskOrThrow(taskId);

        TaskDeadline deadline = new TaskDeadline();
        deadline.setTaskId(taskId);
        deadline.setDeadlineType(type);
        deadline.setDueAt(dueAt);
        deadline.setCreatedAt(LocalDateTime.now());

        TaskDeadline saved = deadlineRepository.save(deadline);

        auditLogService.logTaskOverdue(
                taskId,
                actorUserId
        ); // audit that deadline was added (or create a new action type later)

        return saved;
    }

    /* ============ OVERDUE CHECK ============ */

    @Transactional
    public void checkAndEscalateOverdueTasks() {

        List<TaskDeadline> overdueDeadlines =
                deadlineRepository.findByEscalatedFalseAndDueAtBefore(LocalDateTime.now());

        for (TaskDeadline deadline : overdueDeadlines) {

            Task task = taskService.findTaskOrThrow(deadline.getTaskId());

            boolean violated =
                    (deadline.getDeadlineType() == DeadlineType.START_BY
                            && task.getState() == TaskState.BACKLOG)
                            ||
                            (deadline.getDeadlineType() == DeadlineType.FINISH_BY
                                    && task.getState() != TaskState.DONE);

            if (violated) {
                escalationService.handleOverdueTask(task.getTaskId());
                deadline.setEscalated(true);
                deadlineRepository.save(deadline);
            }
        }
    }
}
