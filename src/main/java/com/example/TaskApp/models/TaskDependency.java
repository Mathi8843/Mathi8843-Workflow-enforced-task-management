package com.example.TaskApp.models;

import jakarta.persistence.*;

@Entity
@Table(
        name = "task_dependencies",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"blocker_task_id", "blocked_task_id"})
        }
)
public class TaskDependency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int dependencyId;

    @Column(name = "blocker_task_id", nullable = false)
    private int blockerTaskId;

    @Column(name = "blocked_task_id", nullable = false)
    private int blockedTaskId;

    public TaskDependency() {}

    public int getDependencyId() {
        return dependencyId;
    }

    public int getBlockerTaskId() {
        return blockerTaskId;
    }

    public void setBlockerTaskId(int blockerTaskId) {
        this.blockerTaskId = blockerTaskId;
    }

    public int getBlockedTaskId() {
        return blockedTaskId;
    }

    public void setBlockedTaskId(int blockedTaskId) {
        this.blockedTaskId = blockedTaskId;
    }
}
