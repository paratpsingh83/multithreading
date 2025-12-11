package com.jobportal.dto;

import com.jobportal.entity.Invoice;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InvoiceStatusDTO {
    @NotNull(message = "Status is required")
    private Invoice.InvoiceStatus status;
}