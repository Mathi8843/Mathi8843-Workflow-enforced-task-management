package com.example.TaskApp.dto;

public class TaskDependencyResponseDTO {
    private int dependencyId;
    private int blockerTaskId;
    private String blockerTaskName;

    public TaskDependencyResponseDTO() {
    }

    public TaskDependencyResponseDTO(int dependencyId, int blockerTaskId, String blockerTaskName) {
        this.dependencyId = dependencyId;
        this.blockerTaskId = blockerTaskId;
        this.blockerTaskName = blockerTaskName;
    }

    public int getDependencyId() {
        return dependencyId;
    }

    public void setDependencyId(int dependencyId) {
        this.dependencyId = dependencyId;
    }

    public int getBlockerTaskId() {
        return blockerTaskId;
    }

    public void setBlockerTaskId(int blockerTaskId) {
        this.blockerTaskId = blockerTaskId;
    }

    public String getBlockerTaskName() {
        return blockerTaskName;
    }

    public void setBlockerTaskName(String blockerTaskName) {
        this.blockerTaskName = blockerTaskName;
    }
}
