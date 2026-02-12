package com.example.TaskApp.controller;

import com.example.TaskApp.auth.AuthUser;
import com.example.TaskApp.dto.TaskRequestDTO;
import com.example.TaskApp.dto.TaskResponseDTO;
import com.example.TaskApp.models.TaskState;
import com.example.TaskApp.service.TaskService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(TaskController.class);

    private final TaskService taskService;
    private final com.example.TaskApp.service.TaskDependencyService taskDependencyService;
    private final com.example.TaskApp.service.DeadlineService deadlineService;

    public TaskController(TaskService taskService,
            com.example.TaskApp.service.TaskDependencyService taskDependencyService,
            com.example.TaskApp.service.DeadlineService deadlineService) {
        this.taskService = taskService;
        this.taskDependencyService = taskDependencyService;
        this.deadlineService = deadlineService;
    }

    /* CREATE TASK */
    @PostMapping
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('MANAGER')")
    public TaskResponseDTO createTask(
            @org.springframework.security.core.annotation.AuthenticationPrincipal AuthUser authUser,
            @RequestBody TaskRequestDTO request) {
        log.info("Create task request by user: {}, payload: {}",
                authUser != null ? authUser.getUsername() : "NULL", request);
        try {
            return taskService.createTask(request, authUser.getUser().getUserId());
        } catch (Exception e) {
            log.error("Failed to create task", e);
            throw e;
        }
    }

    /* GET TASK BY ID */
    @GetMapping("/{id}")
    public TaskResponseDTO getTask(@PathVariable int id) {
        return taskService.getTaskById(id);
    }

    /* GET ALL TASKS */
    @GetMapping
    public List<TaskResponseDTO> getAllTasks() {
        return taskService.getAllTasks();
    }

    /* UPDATE TASK */
    @PutMapping("/{taskId}")
    public TaskResponseDTO updateTask(
            @org.springframework.security.core.annotation.AuthenticationPrincipal com.example.TaskApp.auth.AuthUser authUser,
            @PathVariable int taskId,
            @RequestBody TaskRequestDTO request) {
        return taskService.updateTask(authUser.getUser().getUserId(), taskId, request);
    }

    /* CHANGE TASK STATE */
    @PutMapping("/{taskId}/state")
    public TaskResponseDTO updateTaskState(
            @org.springframework.security.core.annotation.AuthenticationPrincipal com.example.TaskApp.auth.AuthUser authUser,
            @PathVariable int taskId,
            @RequestParam TaskState state) {
        return taskService.moveTaskState(authUser.getUser().getUserId(), taskId, state);
    }

    /* ================= DEPENDENCIES ================= */

    @GetMapping("/{taskId}/dependencies")
    public List<com.example.TaskApp.dto.TaskDependencyResponseDTO> getDependencies(@PathVariable int taskId) {
        return taskDependencyService.getDependenciesForTask(taskId).stream()
                .map(d -> {
                    String blockerName = taskService.getTaskById(d.getBlockerTaskId()).getTaskName();
                    return new com.example.TaskApp.dto.TaskDependencyResponseDTO(d.getDependencyId(),
                            d.getBlockerTaskId(), blockerName);
                })
                .toList();
    }

    @PostMapping("/{taskId}/dependencies")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('MANAGER')")
    public void addDependency(
            @org.springframework.security.core.annotation.AuthenticationPrincipal AuthUser authUser,
            @PathVariable int taskId,
            @RequestParam int blockerId) {
        taskDependencyService.addDependency(authUser.getUser().getUserId(), blockerId, taskId);
    }

    @DeleteMapping("/dependencies/{dependencyId}")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('MANAGER')")
    public void removeDependency(
            @org.springframework.security.core.annotation.AuthenticationPrincipal AuthUser authUser,
            @PathVariable int dependencyId) {
        taskDependencyService.removeDependency(authUser.getUser().getUserId(), dependencyId);
    }

    /* ================= DEADLINES ================= */

    @GetMapping("/{taskId}/deadlines")
    public List<com.example.TaskApp.dto.TaskDeadlineResponseDTO> getDeadlines(@PathVariable int taskId) {
        // Need access to deadlineRepository or a getter in DeadlineService.
        // Let's check DeadlineRepository for taskId query.
        return taskService.getDeadlinesForTask(taskId); // I'll add this to TaskService
    }

    @PostMapping("/{taskId}/deadlines")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('MANAGER')")
    public void addDeadline(
            @org.springframework.security.core.annotation.AuthenticationPrincipal AuthUser authUser,
            @PathVariable int taskId,
            @RequestParam com.example.TaskApp.models.DeadlineType type,
            @RequestParam String dueAt) {
        java.time.LocalDateTime due = java.time.LocalDateTime.parse(dueAt);
        deadlineService.addDeadline(authUser.getUser().getUserId(), taskId, type, due);
    }
}
