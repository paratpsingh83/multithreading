package com.jobportal.service;

import com.jobportal.dto.InvoiceDTO;
import com.jobportal.dto.InvoiceRequestDTO;
import com.jobportal.dto.InvoiceStatsDTO;
import com.jobportal.dto.InvoiceStatusDTO;

import java.util.List;

public interface InvoiceService {
    InvoiceDTO create(InvoiceRequestDTO invoiceRequestDTO);

    InvoiceDTO getById(Long id);

    List<InvoiceDTO> getAll();

    InvoiceDTO update(Long id, InvoiceRequestDTO invoiceRequestDTO);

    void delete(Long id);

    InvoiceDTO updateStatus(Long id, InvoiceStatusDTO statusDTO);

    List<InvoiceDTO> getByPlacementId(Long placementId);

    List<InvoiceDTO> getByStatus(String status);

    InvoiceDTO generateInvoice(Long placementId, String period);

    InvoiceStatsDTO getInvoiceStats();

    List<InvoiceDTO> getOverdueInvoices();
}