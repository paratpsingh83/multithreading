package com.jobportal.serviceImpl;

import com.jobportal.dto.PaymentDTO;
import com.jobportal.dto.PaymentRequestDTO;
import com.jobportal.dto.RevenueMetricsDTO;
import com.jobportal.entity.Invoice;
import com.jobportal.entity.Payment;
import com.jobportal.entity.User;
import com.jobportal.exception.ExceptionUtils;
import com.jobportal.repository.InvoiceRepository;
import com.jobportal.repository.PaymentRepository;
import com.jobportal.repository.UserRepository;
import com.jobportal.service.NotificationService;
import com.jobportal.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public PaymentDTO create(PaymentRequestDTO paymentRequestDTO) {
        Invoice invoice = invoiceRepository.findById(paymentRequestDTO.getInvoiceId())
                .orElseThrow(() -> ExceptionUtils.resourceNotFound("Invoice", "id", paymentRequestDTO.getInvoiceId()));

        // Validate invoice status
        if (invoice.getStatus() != Invoice.InvoiceStatus.SUBMITTED &&
                invoice.getStatus() != Invoice.InvoiceStatus.AWAITING_APPROVAL &&
                invoice.getStatus() != Invoice.InvoiceStatus.OVERDUE) {
            throw ExceptionUtils.businessError("Payment can only be created for submitted, awaiting approval, or overdue invoices");
        }

        // Validate payment amount
        if (paymentRequestDTO.getAmountPaid() == null || paymentRequestDTO.getAmountPaid() <= 0) {
            throw ExceptionUtils.validationError("Payment amount must be greater than zero");
        }

        // Validate payment date
        if (paymentRequestDTO.getPaymentDate().isAfter(LocalDate.now())) {
            throw ExceptionUtils.validationError("Payment date cannot be in the future");
        }

        // Check if payment exceeds invoice amount
        Double totalPaid = getTotalPaidAmount(invoice.getInvoiceId());
        Double remainingAmount = invoice.getTotalAmount() - totalPaid;

        if (paymentRequestDTO.getAmountPaid() > remainingAmount) {
            throw ExceptionUtils.businessError(
                    String.format("Payment amount $%.2f exceeds remaining invoice amount $%.2f",
                            paymentRequestDTO.getAmountPaid(), remainingAmount)
            );
        }

        // Get current user (from security context in real app)
        User currentUser = userRepository.findById(1L)
                .orElseThrow(() -> ExceptionUtils.resourceNotFound("User", "id", 1L));

        Payment payment = new Payment();
        payment.setInvoice(invoice);
        payment.setPaymentDate(paymentRequestDTO.getPaymentDate());
        payment.setAmountPaid(paymentRequestDTO.getAmountPaid());
        payment.setPaymentMethod(paymentRequestDTO.getPaymentMethod());
        payment.setTransactionId(paymentRequestDTO.getTransactionId());
        payment.setNotes(paymentRequestDTO.getNotes());
        payment.setProcessedBy(currentUser);

        Payment savedPayment = paymentRepository.save(payment);

        // Update invoice status if fully paid
        Double newTotalPaid = totalPaid + paymentRequestDTO.getAmountPaid();
        if (newTotalPaid >= invoice.getTotalAmount()) {
            invoice.setStatus(Invoice.InvoiceStatus.PAID);
            invoice.setPaidDate(LocalDate.now());
            invoiceRepository.save(invoice);
        }

        // Notify about payment
        String notificationMessage = String.format(
                "Payment of $%.2f received for invoice %s. Remaining balance: $%.2f",
                paymentRequestDTO.getAmountPaid(),
                invoice.getInvoiceNumber(),
                invoice.getTotalAmount() - newTotalPaid
        );

        notificationService.createNotification(
                invoice.getPlacement().getApplication().getConsultant().getUserId(),
                "Payment Received",
                notificationMessage,
                "PAYMENT",
                savedPayment.getPaymentId()
        );

        // Notify invoice creator
        notificationService.createNotification(
                invoice.getCreatedBy().getUserId(),
                "Payment Recorded",
                String.format("Payment recorded for invoice %s", invoice.getInvoiceNumber()),
                "PAYMENT",
                savedPayment.getPaymentId()
        );

        return PaymentDTO.fromEntity(savedPayment);
    }

    @Override
    public PaymentDTO getById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> ExceptionUtils.resourceNotFound("Payment", "id", id));
        return PaymentDTO.fromEntity(payment);
    }

    @Override
    public List<PaymentDTO> getAll() {
        return paymentRepository.findAll()
                .stream()
                .map(PaymentDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PaymentDTO update(Long id, PaymentRequestDTO paymentRequestDTO) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> ExceptionUtils.resourceNotFound("Payment", "id", id));

        // Only allow updates to notes and transaction ID
        if (paymentRequestDTO.getPaymentMethod() != null) {
            payment.setPaymentMethod(paymentRequestDTO.getPaymentMethod());
        }
        if (paymentRequestDTO.getTransactionId() != null) {
            payment.setTransactionId(paymentRequestDTO.getTransactionId());
        }
        if (paymentRequestDTO.getNotes() != null) {
            payment.setNotes(paymentRequestDTO.getNotes());
        }

        Payment updatedPayment = paymentRepository.save(payment);
        return PaymentDTO.fromEntity(updatedPayment);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> ExceptionUtils.resourceNotFound("Payment", "id", id));

        Invoice invoice = payment.getInvoice();

        // Store amount for recalculation
        Double paymentAmount = payment.getAmountPaid();

        // Delete payment
        paymentRepository.delete(payment);

        // Recalculate invoice status based on remaining payments
        Double totalPaid = getTotalPaidAmount(invoice.getInvoiceId());

        if (totalPaid <= 0) {
            // No payments left, revert to submitted status
            invoice.setStatus(Invoice.InvoiceStatus.SUBMITTED);
            invoice.setPaidDate(null);
        } else if (totalPaid < invoice.getTotalAmount()) {
            // Partial payment, ensure status is appropriate
            if (invoice.getStatus() == Invoice.InvoiceStatus.PAID) {
                invoice.setStatus(Invoice.InvoiceStatus.SUBMITTED);
                invoice.setPaidDate(null);
            }
        }
        // If still fully paid, leave as PAID

        invoiceRepository.save(invoice);
    }

    @Override
    public List<PaymentDTO> getByInvoiceId(Long invoiceId) {
        if (!invoiceRepository.existsById(invoiceId)) {
            throw ExceptionUtils.resourceNotFound("Invoice", "id", invoiceId);
        }

        return paymentRepository.findByInvoiceInvoiceId(invoiceId)
                .stream()
                .map(PaymentDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public RevenueMetricsDTO getPaymentStats() {
        RevenueMetricsDTO metrics = new RevenueMetricsDTO();

        // Total revenue (all time)
        metrics.setTotalRevenue(paymentRepository.sumPaymentsSince(LocalDate.of(2000, 1, 1)));

        // This month's revenue
        metrics.setRevenueThisMonth(paymentRepository.sumPaymentsSince(LocalDate.now().withDayOfMonth(1)));

        // This quarter's revenue
        LocalDate quarterStart = LocalDate.now().withMonth(((LocalDate.now().getMonthValue() - 1) / 3) * 3 + 1)
                .withDayOfMonth(1);
        metrics.setRevenueThisQuarter(paymentRepository.sumPaymentsSince(quarterStart));

        // Outstanding revenue
        Double outstandingRevenue = invoiceRepository.sumOutstandingAmount();
        metrics.setOutstandingRevenue(outstandingRevenue != null ? outstandingRevenue : 0.0);

        return metrics;
    }

    private Double getTotalPaidAmount(Long invoiceId) {
        return paymentRepository.findByInvoiceInvoiceId(invoiceId)
                .stream()
                .mapToDouble(Payment::getAmountPaid)
                .sum();
    }
}