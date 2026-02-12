package com.example.TaskApp.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "task_deadlines")
public class TaskDeadline {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int deadlineId;

    @Column(nullable = false)
    private int taskId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeadlineType deadlineType;

    @Column(nullable = false)
    private LocalDateTime dueAt;

    @Column(nullable = false)
    private boolean escalated = false;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public TaskDeadline() {}

    public int getDeadlineId() {
        return deadlineId;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public DeadlineType getDeadlineType() {
        return deadlineType;
    }

    public void setDeadlineType(DeadlineType deadlineType) {
        this.deadlineType = deadlineType;
    }

    public LocalDateTime getDueAt() {
        return dueAt;
    }

    public void setDueAt(LocalDateTime dueAt) {
        this.dueAt = dueAt;
    }

    public boolean isEscalated() {
        return escalated;
    }

    public void setEscalated(boolean escalated) {
        this.escalated = escalated;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
