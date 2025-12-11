package com.jobportal.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class InvoiceRequestDTO {
    @NotNull(message = "Placement ID is required")
    private Long placementId;
    
    @NotNull(message = "Invoice date is required")
    private LocalDate invoiceDate;
    
    @NotNull(message = "Due date is required")
    private LocalDate dueDate;
    
    private Double taxAmount;
    private String paymentTerms;
    private List<InvoiceItemRequestDTO> items;
}