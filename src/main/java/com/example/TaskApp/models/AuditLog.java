package com.example.TaskApp.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int auditId;

    @Column
    private Integer taskId;

    @Column(nullable = false)
    private int actorUserId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuditActionType actionType;

    @Enumerated(EnumType.STRING)
    private TaskState previousState;

    @Enumerated(EnumType.STRING)
    private TaskState newState;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(length = 500)
    private String comment;


    public AuditLog() {}

    // getters & setters

    public int getAuditId() {
        return auditId;
    }

    public void setAuditId(int auditId) {
        this.auditId = auditId;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public int getActorUserId() {
        return actorUserId;
    }

    public void setActorUserId(int actorUserId) {
        this.actorUserId = actorUserId;
    }

    public AuditActionType getActionType() {
        return actionType;
    }

    public void setActionType(AuditActionType actionType) {
        this.actionType = actionType;
    }

    public TaskState getPreviousState() {
        return previousState;
    }

    public void setPreviousState(TaskState previousState) {
        this.previousState = previousState;
    }

    public TaskState getNewState() {
        return newState;
    }

    public void setNewState(TaskState newState) {
        this.newState = newState;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
