package com.jobportal.dto;

import lombok.Data;

@Data
public class StatusCountDTO {
    private String status;
    private Long count;
    
    public StatusCountDTO(String status, Long count) {
        this.status = status;
        this.count = count;
    }
}