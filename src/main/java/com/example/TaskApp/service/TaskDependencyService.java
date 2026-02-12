package com.example.TaskApp.service;

import com.example.TaskApp.models.Task;
import com.example.TaskApp.models.TaskDependency;
import com.example.TaskApp.models.TaskState;
import com.example.TaskApp.repository.TaskDependencyRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskDependencyService {

    private final TaskDependencyRepository taskDependencyRepository;
    private final TaskQueryService taskQueryService;

    private final AuditLogService auditLogService;

    public TaskDependencyService(
            TaskDependencyRepository taskDependencyRepository,
            TaskQueryService taskQueryService,
            AuditLogService auditLogService
    ) {
        this.taskDependencyRepository = taskDependencyRepository;
this.taskQueryService = taskQueryService;
        this.auditLogService = auditLogService;
    }

    /* ================= ADD ================= */

    public TaskDependency addDependency(
            int actorUserId,
            int blockerTaskId,
            int blockedTaskId
    ) {

        if (blockerTaskId == blockedTaskId) {
            throw new IllegalArgumentException("Task cannot depend on itself");
        }

        taskQueryService.getTaskOrThrow(blockerTaskId);
        taskQueryService.getTaskOrThrow(blockedTaskId);


        if (taskDependencyRepository
                .existsByBlockerTaskIdAndBlockedTaskId(blockerTaskId, blockedTaskId)) {
            throw new IllegalArgumentException("Dependency already exists");
        }

        TaskDependency dependency = new TaskDependency();
        dependency.setBlockerTaskId(blockerTaskId);
        dependency.setBlockedTaskId(blockedTaskId);

        TaskDependency saved = taskDependencyRepository.save(dependency);
        auditLogService.logDependencyAdded(blockedTaskId, actorUserId, blockerTaskId);

        return saved;
    }

    /* ================= REMOVE ================= */

    public void removeDependency(int actorUserId, int dependencyId) {

        TaskDependency dependency = taskDependencyRepository.findById(dependencyId)
                .orElseThrow(() -> new IllegalArgumentException("Dependency not found"));

        taskDependencyRepository.delete(dependency);
        auditLogService.logDependencyRemoved(
                dependency.getBlockedTaskId(),
                actorUserId,
                dependency.getBlockerTaskId()
        );
    }

    /* ================= QUERY ================= */

    public List<TaskDependency> getDependenciesForTask(int taskId) {
        return taskDependencyRepository.findByBlockedTaskId(taskId);
    }

    /* ================= BLOCK CHECK ================= */

    public boolean hasUnresolvedDependencies(int taskId) {

        List<TaskDependency> dependencies =
                taskDependencyRepository.findByBlockedTaskId(taskId);

        for (TaskDependency dependency : dependencies) {
            Task blocker = taskQueryService.getTaskOrThrow(dependency.getBlockerTaskId());


            if (blocker.getState() != TaskState.DONE) {
                return true;
            }
        }
        return false;
    }
}
