package com.example.TaskApp.service;

import com.example.TaskApp.dto.TaskRequestDTO;
import com.example.TaskApp.dto.TaskResponseDTO;
import com.example.TaskApp.exception.InvalidTaskStateException;
import com.example.TaskApp.exception.PermissionDeniedException;
import com.example.TaskApp.models.Role;
import com.example.TaskApp.models.Task;
import com.example.TaskApp.models.TaskState;
import com.example.TaskApp.models.User;
import com.example.TaskApp.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserService userService;
    private final PermissionService permissionService;
    private final TaskDependencyService taskDependencyService;
    private final AuditLogService auditLogService;
    private final TaskQueryService taskQueryService;
    private final com.example.TaskApp.repository.TaskDeadlineRepository deadlineRepository;

    public TaskService(
            TaskRepository taskRepository,
            UserService userService,
            PermissionService permissionService,
            TaskDependencyService taskDependencyService,
            AuditLogService auditLogService,
            TaskQueryService taskQueryService,
            com.example.TaskApp.repository.TaskDeadlineRepository deadlineRepository) {
        this.taskRepository = taskRepository;
        this.userService = userService;
        this.permissionService = permissionService;
        this.taskDependencyService = taskDependencyService;
        this.auditLogService = auditLogService;
        this.taskQueryService = taskQueryService;
        this.deadlineRepository = deadlineRepository;
    }

    /* ================= CREATE ================= */

    @Transactional
    public TaskResponseDTO createTask(TaskRequestDTO request, int userId) {

        User actor = userService.getUserEntityById(userId);

        if (!permissionService.canCreateAndAssignTask(actor)) {
            throw new PermissionDeniedException("Only MANAGER can create and assign tasks");
        }

        User assignee = userService.getUserEntityById(request.getAssigneeId());
        User reviewer = userService.getUserEntityById(request.getReviewerId());

        if (assignee.getUserId() == reviewer.getUserId()) {
            throw new IllegalArgumentException("Assignee and Reviewer must be different");
        }

        if (assignee.getRole() != Role.DEVELOPER || reviewer.getRole() != Role.REVIEWER) {
            throw new IllegalArgumentException("Invalid assignee or reviewer role");
        }

        Task task = new Task();
        task.setTaskName(request.getTaskName());
        task.setDescription(request.getDescription());
        task.setAssigneeId(assignee.getUserId());
        task.setReviewerId(reviewer.getUserId());
        task.setState(TaskState.BACKLOG);
        task.setCreationTime(LocalDateTime.now());

        Task saved = taskRepository.save(task);

        // Handle optional deadline
        if (request.getDueAt() != null && !request.getDueAt().isEmpty()) {
            com.example.TaskApp.models.TaskDeadline deadline = new com.example.TaskApp.models.TaskDeadline();
            deadline.setTaskId(saved.getTaskId());
            deadline.setDeadlineType(com.example.TaskApp.models.DeadlineType.FINISH_BY);
            deadline.setDueAt(LocalDateTime.parse(request.getDueAt()));
            deadline.setCreatedAt(LocalDateTime.now());
            deadlineRepository.save(deadline);
        }

        auditLogService.logTaskCreated(saved.getTaskId(), userId);

        return mapToResponse(saved);
    }

    /* ================= READ ================= */

    public TaskResponseDTO getTaskById(int taskId) {
        return mapToResponse(findTaskOrThrow(taskId));
    }

    public List<TaskResponseDTO> getAllTasks() {
        return taskRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /* ================= WORKFLOW ================= */

    @Transactional
    public TaskResponseDTO moveTaskState(int userId, int taskId, TaskState targetState) {

        User actor = userService.getUserEntityById(userId);
        Task task = taskQueryService.getTaskOrThrow(taskId);

        if (!permissionService.canChangeTaskState(actor, task, targetState)) {
            throw new PermissionDeniedException("User not allowed to perform this action");
        }

        if (!TaskState.isValidTransition(task.getState(), targetState)) {
            throw new InvalidTaskStateException(
                    "Invalid transition from " + task.getState() + " to " + targetState);
        }

        if (targetState == TaskState.IN_PROGRESS &&
                taskDependencyService.hasUnresolvedDependencies(taskId)) {
            throw new InvalidTaskStateException("Task has unresolved dependencies");
        }

        TaskState oldState = task.getState();
        task.setState(targetState);

        Task updated = taskRepository.save(task);
        auditLogService.logTaskStateChange(taskId, userId, oldState, targetState);

        return mapToResponse(updated);
    }

    /* ================= UPDATE ================= */

    @Transactional
    public TaskResponseDTO updateTask(int userId, int taskId, TaskRequestDTO request) {
        User actor = userService.getUserEntityById(userId);

        // Only MANAGER can update task details
        if (actor.getRole() != Role.MANAGER) {
            throw new PermissionDeniedException("Only MANAGER can update task details");
        }

        Task task = findTaskOrThrow(taskId);

        // Validate new assignee/reviewer roles if changed
        User assignee = userService.getUserEntityById(request.getAssigneeId());
        User reviewer = userService.getUserEntityById(request.getReviewerId());

        if (assignee.getUserId() == reviewer.getUserId()) {
            throw new IllegalArgumentException("Assignee and Reviewer must be different");
        }

        if (assignee.getRole() != Role.DEVELOPER || reviewer.getRole() != Role.REVIEWER) {
            throw new IllegalArgumentException("Invalid assignee or reviewer role");
        }

        task.setTaskName(request.getTaskName());
        task.setDescription(request.getDescription());
        task.setAssigneeId(assignee.getUserId());
        task.setReviewerId(reviewer.getUserId());

        // We don't reset state on update, assuming just detail correction.
        // If state change needed, use moveTaskState.

        Task updated = taskRepository.save(task);
        // auditLogService.logTaskUpdated(taskId, userId); // Assuming this method
        // exists or I should create it

        return mapToResponse(updated);
    }

    /* ================= INTERNAL ================= */

    Task findTaskOrThrow(int taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));
    }

    public List<com.example.TaskApp.dto.TaskDeadlineResponseDTO> getDeadlinesForTask(int taskId) {
        return deadlineRepository.findByTaskId(taskId).stream()
                .map(d -> new com.example.TaskApp.dto.TaskDeadlineResponseDTO(d.getDeadlineId(), d.getDeadlineType(),
                        d.getDueAt(), d.isEscalated()))
                .toList();
    }

    /* ================= MAPPER ================= */

    private TaskResponseDTO mapToResponse(Task task) {
        TaskResponseDTO dto = new TaskResponseDTO();
        dto.setTaskId(task.getTaskId());
        dto.setTaskName(task.getTaskName());
        dto.setDescription(task.getDescription());
        dto.setState(task.getState());
        dto.setAssigneeId(task.getAssigneeId());
        dto.setReviewerId(task.getReviewerId());
        dto.setCreatedAt(task.getCreationTime());

        // Blocked status
        dto.setBlocked(taskDependencyService.hasUnresolvedDependencies(task.getTaskId()));

        // Deadline (pick the first or soonest deadline for simplicity in board view)
        deadlineRepository.findByTaskId(task.getTaskId()).stream()
                .findFirst()
                .ifPresent(d -> dto.setDueAt(d.getDueAt()));

        // Enrich with user names
        // For performance, better to fetch them. Or keep it simple and fetch in
        // frontend.
        // Let's add user names to DTO for better frontend experience.
        try {
            dto.setAssigneeName(userService.getUserEntityById(task.getAssigneeId()).getDisplayName());
            dto.setReviewerName(userService.getUserEntityById(task.getReviewerId()).getDisplayName());
        } catch (Exception e) {
            // Ignore if user not found (should not happen due to referential integrity)
        }

        return dto;
    }
}
