package com.jobportal.service;

import com.jobportal.dto.PaymentDTO;
import com.jobportal.dto.PaymentRequestDTO;
import com.jobportal.dto.RevenueMetricsDTO;

import java.util.List;

public interface PaymentService {
    PaymentDTO create(PaymentRequestDTO paymentRequestDTO);

    PaymentDTO getById(Long id);

    List<PaymentDTO> getAll();

    PaymentDTO update(Long id, PaymentRequestDTO paymentRequestDTO);

    void delete(Long id);

    List<PaymentDTO> getByInvoiceId(Long invoiceId);

    RevenueMetricsDTO getPaymentStats();
}