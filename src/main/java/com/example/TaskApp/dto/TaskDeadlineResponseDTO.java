package com.example.TaskApp.dto;

import com.example.TaskApp.models.DeadlineType;
import java.time.LocalDateTime;

public class TaskDeadlineResponseDTO {
    private int deadlineId;
    private DeadlineType deadlineType;
    private LocalDateTime dueAt;
    private boolean escalated;

    public TaskDeadlineResponseDTO() {
    }

    public TaskDeadlineResponseDTO(int deadlineId, DeadlineType deadlineType, LocalDateTime dueAt, boolean escalated) {
        this.deadlineId = deadlineId;
        this.deadlineType = deadlineType;
        this.dueAt = dueAt;
        this.escalated = escalated;
    }

    public int getDeadlineId() {
        return deadlineId;
    }

    public void setDeadlineId(int deadlineId) {
        this.deadlineId = deadlineId;
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
}
