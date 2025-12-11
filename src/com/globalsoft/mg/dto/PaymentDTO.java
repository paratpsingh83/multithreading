package com.jobportal.dto;

import com.jobportal.entity.Payment;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PaymentDTO {
    private Long paymentId;
    private Long invoiceId;
    private LocalDate paymentDate;
    private Double amountPaid;
    private String paymentMethod;
    private String transactionId;
    private String notes;
    private UserDTO processedBy;
    private LocalDateTime processedAt;
    
    public static PaymentDTO fromEntity(Payment payment) {
        PaymentDTO dto = new PaymentDTO();
        dto.setPaymentId(payment.getPaymentId());
        dto.setInvoiceId(payment.getInvoice().getInvoiceId());
        dto.setPaymentDate(payment.getPaymentDate());
        dto.setAmountPaid(payment.getAmountPaid());
        dto.setPaymentMethod(payment.getPaymentMethod());
        dto.setTransactionId(payment.getTransactionId());
        dto.setNotes(payment.getNotes());
        
        if (payment.getProcessedBy() != null) {
            dto.setProcessedBy(UserDTO.fromEntity(payment.getProcessedBy()));
        }
        
        dto.setProcessedAt(payment.getProcessedAt());
        return dto;
    }
}