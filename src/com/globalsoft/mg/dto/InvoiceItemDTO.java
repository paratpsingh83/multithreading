package com.jobportal.dto;

import com.jobportal.entity.InvoiceItem;
import lombok.Data;

@Data
public class InvoiceItemDTO {
    private Long itemId;
    private Long invoiceId;
    private String description;
    private Integer quantity;
    private Double unitPrice;
    private Double totalAmount;
    private TimesheetDTO timesheet;
    
    public static InvoiceItemDTO fromEntity(InvoiceItem item) {
        InvoiceItemDTO dto = new InvoiceItemDTO();
        dto.setItemId(item.getItemId());
        dto.setInvoiceId(item.getInvoice().getInvoiceId());
        dto.setDescription(item.getDescription());
        dto.setQuantity(item.getQuantity());
        dto.setUnitPrice(item.getUnitPrice());
        dto.setTotalAmount(item.getTotalAmount());
        
        if (item.getTimesheet() != null) {
            dto.setTimesheet(TimesheetDTO.fromEntity(item.getTimesheet()));
        }
        
        return dto;
    }
}