package com.example.TaskApp.models;

public enum TaskState {
    BACKLOG,
    IN_PROGRESS,
    REVIEW,
    DONE;

    public static boolean isValidTransition(TaskState from, TaskState to) {
        return switch (from) {
            case BACKLOG -> to == IN_PROGRESS;
            case IN_PROGRESS -> to == REVIEW;
            case REVIEW -> to == DONE || to == IN_PROGRESS;
            case DONE -> false; 
        };
    }
}
