package com.example.TaskApp.service;

import com.example.TaskApp.models.Task;
import com.example.TaskApp.repository.TaskRepository;
import org.springframework.stereotype.Service;

@Service
public class TaskQueryService {

    private final TaskRepository taskRepository;

    public TaskQueryService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task getTaskOrThrow(int taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));
    }
}
