package com.jobportal.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.Map;

@Data
public class ErrorResponseDTO {
    private int status;
    private String message;
    private Map<String, String> errors;
    private LocalDateTime timestamp;
    private String path;
    
    public ErrorResponseDTO(int status, String message, String path) {
        this.status = status;
        this.message = message;
        this.timestamp = LocalDateTime.now();
        this.path = path;
    }
    
    public ErrorResponseDTO(int status, String message, Map<String, String> errors, String path) {
        this.status = status;
        this.message = message;
        this.errors = errors;
        this.timestamp = LocalDateTime.now();
        this.path = path;
    }
}