package com.example.TaskApp.models;

public enum AuditActionType {
    TASK_CREATED,
    TASK_STATE_CHANGED,
    TASK_ASSIGNED,
    DEPENDENCY_ADDED,
    DEPENDENCY_REMOVED,
    PERMISSION_DENIED,
    TASK_OVERDUE,
    TASK_ESCALATED
}

