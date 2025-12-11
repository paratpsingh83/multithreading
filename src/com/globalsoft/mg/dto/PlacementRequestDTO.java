package com.jobportal.dto;

import com.jobportal.entity.Placement;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class PlacementRequestDTO {
    @NotNull(message = "Application ID is required")
    private Long applicationId;
    
    @NotNull(message = "Start date is required")
    private LocalDate startDate;
    
    private LocalDate endDate;
    
    @NotNull(message = "Contract type is required")
    private Placement.ContractType contractType;
    
    @NotNull(message = "Billing rate is required")
    private Double billingRate;
    
    private Double clientBillRate;
    
    private Placement.PlacementStatus placementStatus = Placement.PlacementStatus.ACTIVE;
}