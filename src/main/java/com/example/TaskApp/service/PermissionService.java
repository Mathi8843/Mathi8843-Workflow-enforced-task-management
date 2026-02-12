package com.example.TaskApp.service;

import com.example.TaskApp.models.Role;
import com.example.TaskApp.models.Task;
import com.example.TaskApp.models.TaskState;
import com.example.TaskApp.models.User;
import org.springframework.stereotype.Service;

@Service
public class PermissionService {

    /* MANAGER */

    public boolean canCreateAndAssignTask(User user) {
        return user.getRole() == Role.MANAGER;
    }

    /* DEVELOPER */

    private boolean canStartTask(User user, Task task, TaskState target) {
        return user.getRole() == Role.DEVELOPER
                && user.getUserId() == task.getAssigneeId()
                && task.getState() == TaskState.BACKLOG
                && target == TaskState.IN_PROGRESS;
    }

    private boolean canSubmitForReview(User user, Task task, TaskState target) {
        return user.getRole() == Role.DEVELOPER
                && user.getUserId() == task.getAssigneeId()
                && task.getState() == TaskState.IN_PROGRESS
                && target == TaskState.REVIEW;
    }

    private boolean canRejectTask(User user, Task task, TaskState target) {
        return user.getRole() == Role.REVIEWER
                && user.getUserId() == task.getReviewerId()
                && task.getState() == TaskState.REVIEW
                && target == TaskState.IN_PROGRESS;
    }

    /* REVIEWER */

    private boolean canApproveTask(User user, Task task, TaskState target) {
        return user.getRole() == Role.REVIEWER
                && user.getUserId() == task.getReviewerId()
                && task.getState() == TaskState.REVIEW
                && target == TaskState.DONE;
    }

    /* CENTRAL DECISION */

    public boolean canChangeTaskState(User user, Task task, TaskState target) {

        return canStartTask(user, task, target)
                || canSubmitForReview(user, task, target)
                || canRejectTask(user, task, target)
                || canApproveTask(user, task, target);
    }
}
