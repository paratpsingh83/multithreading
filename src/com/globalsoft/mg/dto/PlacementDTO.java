package com.jobportal.dto;

import com.jobportal.entity.Placement;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PlacementDTO {
    private Long placementId;
    private ApplicationDTO application;
    private LocalDate startDate;
    private LocalDate endDate;
    private Placement.ContractType contractType;
    private Double billingRate;
    private Double clientBillRate;
    private Placement.PlacementStatus placementStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public static PlacementDTO fromEntity(Placement placement) {
        PlacementDTO dto = new PlacementDTO();
        dto.setPlacementId(placement.getPlacementId());
        dto.setApplication(ApplicationDTO.fromEntity(placement.getApplication()));
        dto.setStartDate(placement.getStartDate());
        dto.setEndDate(placement.getEndDate());
        dto.setContractType(placement.getContractType());
        dto.setBillingRate(placement.getBillingRate());
        dto.setClientBillRate(placement.getClientBillRate());
        dto.setPlacementStatus(placement.getPlacementStatus());
        dto.setCreatedAt(placement.getCreatedAt());
        dto.setUpdatedAt(placement.getUpdatedAt());
        return dto;
    }
}