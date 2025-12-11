package com.globalsoft.mg;

import com.jobportal.dto.*;
import com.jobportal.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService service;

    @GetMapping("/api/v1/audit-logs")
    public ResponseEntity<ApiResponseDTO<List<AuditLogDTO>>> getAuditLogs() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseDTO.success(service.getAuditLogs(),"Audit logs retrieved successfully"));
    }

    @GetMapping("/api/v1/system-health")
    public ResponseEntity<ApiResponseDTO<SystemHealthDTO>> getSystemHealth() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseDTO.success( service.getSystemHealth(),"System health retrieved successfully"));
    }

    @GetMapping("/api/v1/user-activity")
    public ResponseEntity<ApiResponseDTO<List<UserActivityDTO>>> getUserActivity() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseDTO.success( service.getUserActivity(),"User activity retrieved successfully"));
    }

    @PostMapping("/api/v1/backup")
    public ResponseEntity<ApiResponseDTO<String>> createBackup() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseDTO.success("Backup created successfully", service.createBackup()));
    }
}