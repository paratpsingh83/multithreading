package com.jobportal.dto;

import com.jobportal.entity.Invoice;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class InvoiceDTO {
    private Long invoiceId;
    private PlacementDTO placement;
    private String invoiceNumber;
    private LocalDate invoiceDate;
    private LocalDate dueDate;
    private Double totalAmount;
    private Double taxAmount;
    private Invoice.InvoiceStatus status;
    private UserDTO createdBy;
    private LocalDateTime submittedDate;
    private LocalDate paidDate;
    private String paymentTerms;
    private List<InvoiceItemDTO> items;
    private List<PaymentDTO> payments;
    
    public static InvoiceDTO fromEntity(Invoice invoice) {
        InvoiceDTO dto = new InvoiceDTO();
        dto.setInvoiceId(invoice.getInvoiceId());
        dto.setPlacement(PlacementDTO.fromEntity(invoice.getPlacement()));
        dto.setInvoiceNumber(invoice.getInvoiceNumber());
        dto.setInvoiceDate(invoice.getInvoiceDate());
        dto.setDueDate(invoice.getDueDate());
        dto.setTotalAmount(invoice.getTotalAmount());
        dto.setTaxAmount(invoice.getTaxAmount());
        dto.setStatus(invoice.getStatus());
        dto.setCreatedBy(UserDTO.fromEntity(invoice.getCreatedBy()));
        dto.setSubmittedDate(invoice.getSubmittedDate());
        dto.setPaidDate(invoice.getPaidDate());
        dto.setPaymentTerms(invoice.getPaymentTerms());
        return dto;
    }
}