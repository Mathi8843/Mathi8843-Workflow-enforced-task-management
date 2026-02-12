package com.example.TaskApp.dto;

public class TaskRequestDTO {

    private String taskName;
    private String description;
    private int assigneeId;
    private int reviewerId;

    public TaskRequestDTO() {
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getAssigneeId() {
        return assigneeId;
    }

    public void setAssigneeId(int assigneeId) {
        this.assigneeId = assigneeId;
    }

    public int getReviewerId() {
        return reviewerId;
    }

    public void setReviewerId(int reviewerId) {
        this.reviewerId = reviewerId;
    }

    private String dueAt;

    public String getDueAt() {
        return dueAt;
    }

    public void setDueAt(String dueAt) {
        this.dueAt = dueAt;
    }

    @Override
    public String toString() {
        return "TaskRequestDTO{" +
                "taskName='" + taskName + '\'' +
                ", description='" + description + '\'' +
                ", assigneeId=" + assigneeId +
                ", reviewerId=" + reviewerId +
                '}';
    }
}
