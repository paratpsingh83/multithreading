package com.jobportal.repository;

import com.jobportal.entity.InvoiceItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceItemRepository extends JpaRepository<InvoiceItem, Long> {
    List<InvoiceItem> findByInvoiceInvoiceId(Long invoiceId);

    List<InvoiceItem> findByTimesheetTimesheetId(Long timesheetId);
}