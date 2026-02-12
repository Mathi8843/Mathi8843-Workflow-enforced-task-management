package com.example.TaskApp.controller;

import com.example.TaskApp.models.AuditLog;
import com.example.TaskApp.service.AuditLogService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
@CrossOrigin("*")
public class AuditLogController {

    private final AuditLogService auditLogService;

    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    /* GET TASK HISTORY */
    @GetMapping("/{taskId}/history")
    public List<AuditLog> getTaskHistory(@PathVariable int taskId) {
        return auditLogService.getLogsForTask(taskId);
    }
}
