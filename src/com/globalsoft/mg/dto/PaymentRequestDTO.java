package com.jobportal.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class PaymentRequestDTO {
    @NotNull(message = "Invoice ID is required")
    private Long invoiceId;
    
    @NotNull(message = "Payment date is required")
    private LocalDate paymentDate;
    
    @NotNull(message = "Amount paid is required")
    private Double amountPaid;
    
    private String paymentMethod;
    private String transactionId;
    private String notes;
}