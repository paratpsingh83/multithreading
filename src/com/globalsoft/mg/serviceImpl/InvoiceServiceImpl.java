package com.jobportal.serviceImpl;

import com.jobportal.dto.*;
import com.jobportal.entity.*;
import com.jobportal.exception.ExceptionUtils;
import com.jobportal.repository.*;
import com.jobportal.service.InvoiceService;
import com.jobportal.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceItemRepository invoiceItemRepository;
    private final PlacementRepository placementRepository;
    private final TimesheetRepository timesheetRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final PaymentRepository paymentRepository;

    private final AtomicLong invoiceCounter = new AtomicLong(1000L);

    @Override
    @Transactional
    public InvoiceDTO create(InvoiceRequestDTO invoiceRequestDTO) {
        Placement placement = placementRepository.findById(invoiceRequestDTO.getPlacementId())
                .orElseThrow(() -> ExceptionUtils.resourceNotFound("Placement", "id", invoiceRequestDTO.getPlacementId()));

        // Validate placement is active
        if (placement.getPlacementStatus() != Placement.PlacementStatus.ACTIVE) {
            throw ExceptionUtils.businessError("Cannot create invoice for inactive placement");
        }

        // Validate dates
        validateInvoiceDates(invoiceRequestDTO.getInvoiceDate(), invoiceRequestDTO.getDueDate());

        // Generate invoice number
        String invoiceNumber = generateInvoiceNumber();

        // Get current user (from security context in real app)
        User currentUser = userRepository.findById(1L)
                .orElseThrow(() -> ExceptionUtils.resourceNotFound("User", "id", 1L));

        Invoice invoice = new Invoice();
        invoice.setPlacement(placement);
        invoice.setInvoiceNumber(invoiceNumber);
        invoice.setInvoiceDate(invoiceRequestDTO.getInvoiceDate());
        invoice.setDueDate(invoiceRequestDTO.getDueDate());
        invoice.setTaxAmount(invoiceRequestDTO.getTaxAmount() != null ? invoiceRequestDTO.getTaxAmount() : 0.0);
        invoice.setPaymentTerms(invoiceRequestDTO.getPaymentTerms());
        invoice.setStatus(Invoice.InvoiceStatus.DRAFT);
        invoice.setCreatedBy(currentUser);

        // Calculate total amount from items
        Double totalAmount = calculateTotalAmount(invoiceRequestDTO.getItems());
        invoice.setTotalAmount(totalAmount);

        Invoice savedInvoice = invoiceRepository.save(invoice);

        // Create invoice items
        if (invoiceRequestDTO.getItems() != null && !invoiceRequestDTO.getItems().isEmpty()) {
            for (InvoiceItemRequestDTO itemDTO : invoiceRequestDTO.getItems()) {
                createInvoiceItem(savedInvoice, itemDTO);
            }
        }

        return InvoiceDTO.fromEntity(savedInvoice);
    }

    @Override
    public InvoiceDTO getById(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> ExceptionUtils.resourceNotFound("Invoice", "id", id));

        // Load items for the invoice
        invoice.setItems(invoiceItemRepository.findByInvoiceInvoiceId(id));

        return InvoiceDTO.fromEntity(invoice);
    }

    @Override
    public List<InvoiceDTO> getAll() {
        return invoiceRepository.findAll()
                .stream()
                .map(invoice -> {
                    invoice.setItems(invoiceItemRepository.findByInvoiceInvoiceId(invoice.getInvoiceId()));
                    return InvoiceDTO.fromEntity(invoice);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public InvoiceDTO update(Long id, InvoiceRequestDTO invoiceRequestDTO) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> ExceptionUtils.resourceNotFound("Invoice", "id", id));

        // Only allow updates to draft invoices
        if (invoice.getStatus() != Invoice.InvoiceStatus.DRAFT) {
            throw ExceptionUtils.businessError("Cannot update submitted or paid invoice");
        }

        // Update allowed fields
        if (invoiceRequestDTO.getInvoiceDate() != null) {
            invoice.setInvoiceDate(invoiceRequestDTO.getInvoiceDate());
        }
        if (invoiceRequestDTO.getDueDate() != null) {
            invoice.setDueDate(invoiceRequestDTO.getDueDate());
        }
        if (invoiceRequestDTO.getTaxAmount() != null) {
            invoice.setTaxAmount(invoiceRequestDTO.getTaxAmount());
        }
        if (invoiceRequestDTO.getPaymentTerms() != null) {
            invoice.setPaymentTerms(invoiceRequestDTO.getPaymentTerms());
        }

        // Recalculate total if items are provided
        if (invoiceRequestDTO.getItems() != null && !invoiceRequestDTO.getItems().isEmpty()) {
            // Remove existing items
            invoiceItemRepository.deleteAll(invoice.getItems());

            // Add new items
            for (InvoiceItemRequestDTO itemDTO : invoiceRequestDTO.getItems()) {
                createInvoiceItem(invoice, itemDTO);
            }

            // Recalculate total
            Double totalAmount = calculateTotalAmount(invoiceRequestDTO.getItems());
            invoice.setTotalAmount(totalAmount);
        }

        Invoice updatedInvoice = invoiceRepository.save(invoice);
        return InvoiceDTO.fromEntity(updatedInvoice);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> ExceptionUtils.resourceNotFound("Invoice", "id", id));

        if (invoice.getStatus() != Invoice.InvoiceStatus.DRAFT) {
            throw ExceptionUtils.businessError("Cannot delete submitted or paid invoice");
        }

        // Delete all items first
        invoiceItemRepository.deleteAll(invoice.getItems());
        invoiceRepository.delete(invoice);
    }

    @Override
    @Transactional
    public InvoiceDTO updateStatus(Long id, InvoiceStatusDTO statusDTO) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> ExceptionUtils.resourceNotFound("Invoice", "id", id));

        Invoice.InvoiceStatus newStatus = statusDTO.getStatus();
        Invoice.InvoiceStatus oldStatus = invoice.getStatus();

        // Validate status transition
        validateInvoiceStatusTransition(oldStatus, newStatus);

        invoice.setStatus(newStatus);

        if (newStatus == Invoice.InvoiceStatus.SUBMITTED) {
            invoice.setSubmittedDate(java.time.LocalDateTime.now());
        }

        Invoice updatedInvoice = invoiceRepository.save(invoice);

        // Notify about status change
        String notificationMessage = String.format(
                "Invoice %s status changed to %s. Amount: $%.2f",
                invoice.getInvoiceNumber(), newStatus, invoice.getTotalAmount()
        );

        notificationService.createNotification(
                invoice.getPlacement().getApplication().getConsultant().getUserId(),
                "Invoice Status Updated",
                notificationMessage,
                "INVOICE",
                updatedInvoice.getInvoiceId()
        );

        return InvoiceDTO.fromEntity(updatedInvoice);
    }

    @Override
    public List<InvoiceDTO> getByPlacementId(Long placementId) {
        if (!placementRepository.existsById(placementId)) {
            throw ExceptionUtils.resourceNotFound("Placement", "id", placementId);
        }

        return invoiceRepository.findByPlacementPlacementId(placementId)
                .stream()
                .map(invoice -> {
                    invoice.setItems(invoiceItemRepository.findByInvoiceInvoiceId(invoice.getInvoiceId()));
                    return InvoiceDTO.fromEntity(invoice);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<InvoiceDTO> getByStatus(String status) {
        try {
            Invoice.InvoiceStatus invoiceStatus = Invoice.InvoiceStatus.valueOf(status.toUpperCase());
            return invoiceRepository.findByStatus(invoiceStatus)
                    .stream()
                    .map(invoice -> {
                        invoice.setItems(invoiceItemRepository.findByInvoiceInvoiceId(invoice.getInvoiceId()));
                        return InvoiceDTO.fromEntity(invoice);
                    })
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw ExceptionUtils.validationError("Invalid invoice status: " + status);
        }
    }

    @Override
    @Transactional
    public InvoiceDTO generateInvoice(Long placementId, String period) {
        Placement placement = placementRepository.findById(placementId)
                .orElseThrow(() -> ExceptionUtils.resourceNotFound("Placement", "id", placementId));

        // Find timesheets for the specified period
        List<Timesheet> timesheets = findTimesheetsForPeriod(placement, period);

        if (timesheets.isEmpty()) {
            throw ExceptionUtils.businessError("No timesheets found for the specified period: " + period);
        }

        // Calculate total hours and amount
        double totalHours = timesheets.stream()
                .mapToDouble(t -> t.getTotalHours() != null ? t.getTotalHours() : 0.0)
                .sum();

        double totalAmount = totalHours * placement.getBillingRate();

        // Create invoice request
        InvoiceRequestDTO invoiceRequest = new InvoiceRequestDTO();
        invoiceRequest.setPlacementId(placementId);
        invoiceRequest.setInvoiceDate(LocalDate.now());
        invoiceRequest.setDueDate(LocalDate.now().plusDays(30));
        invoiceRequest.setPaymentTerms("Net 30");

        // Create invoice items from timesheets
        List<InvoiceItemRequestDTO> items = timesheets.stream()
                .map(this::createInvoiceItemFromTimesheet)
                .collect(Collectors.toList());
        invoiceRequest.setItems(items);

        return create(invoiceRequest);
    }

    @Override
    public InvoiceStatsDTO getInvoiceStats() {
        InvoiceStatsDTO stats = new InvoiceStatsDTO();
        LocalDate monthStart = LocalDate.now().withDayOfMonth(1);

        Map<String, Long> statusCounts = new HashMap<>();
        for (Invoice.InvoiceStatus status : Invoice.InvoiceStatus.values()) {
            statusCounts.put(status.name(), invoiceRepository.countByStatus(status));
        }

        stats.setStatusCounts(statusCounts);
        stats.setTotalInvoices(invoiceRepository.count());
        stats.setPendingReviewCount(invoiceRepository.countPendingReview());
        stats.setSubmittedCount(invoiceRepository.countSubmitted());
        stats.setAwaitingApprovalCount(invoiceRepository.countAwaitingApproval());

        // Fix: Handle null values for outstanding amount
        Double outstanding = invoiceRepository.sumOutstandingAmount();
        stats.setTotalOutstanding(outstanding != null ? outstanding : 0.0);

        // Fix: Use payment repository for paid amounts instead of invoice repository
        Double paidThisMonth = paymentRepository.sumPaymentsSince(monthStart);
        stats.setTotalPaidThisMonth(paidThisMonth != null ? paidThisMonth : 0.0);

        return stats;
    }

    @Override
    public List<InvoiceDTO> getOverdueInvoices() {
        return invoiceRepository.findOverdueInvoices()
                .stream()
                .map(invoice -> {
                    invoice.setItems(invoiceItemRepository.findByInvoiceInvoiceId(invoice.getInvoiceId()));
                    return InvoiceDTO.fromEntity(invoice);
                })
                .collect(Collectors.toList());
    }

    private String generateInvoiceNumber() {
        String prefix = "INV";
        String timestamp = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String sequence = String.format("%04d", invoiceCounter.incrementAndGet() % 10000);
        return prefix + "-" + timestamp + "-" + sequence;
    }

    private Double calculateTotalAmount(List<InvoiceItemRequestDTO> items) {
        if (items == null || items.isEmpty()) {
            return 0.0;
        }

        return items.stream()
                .mapToDouble(item -> item.getQuantity() * item.getUnitPrice())
                .sum();
    }

    private void createInvoiceItem(Invoice invoice, InvoiceItemRequestDTO itemDTO) {
        // Validate item
        if (itemDTO.getQuantity() == null || itemDTO.getQuantity() <= 0) {
            throw ExceptionUtils.validationError("Item quantity must be greater than 0");
        }
        if (itemDTO.getUnitPrice() == null || itemDTO.getUnitPrice() <= 0) {
            throw ExceptionUtils.validationError("Item unit price must be greater than 0");
        }

        InvoiceItem item = new InvoiceItem();
        item.setInvoice(invoice);
        item.setDescription(itemDTO.getDescription());
        item.setQuantity(itemDTO.getQuantity());
        item.setUnitPrice(itemDTO.getUnitPrice());
        item.setTotalAmount(itemDTO.getQuantity() * itemDTO.getUnitPrice());

        // Link timesheet if provided
        if (itemDTO.getTimesheetId() != null) {
            Timesheet timesheet = timesheetRepository.findById(itemDTO.getTimesheetId())
                    .orElseThrow(() -> ExceptionUtils.resourceNotFound("Timesheet", "id", itemDTO.getTimesheetId()));
            item.setTimesheet(timesheet);
        }

        invoiceItemRepository.save(item);
    }

    private List<Timesheet> findTimesheetsForPeriod(Placement placement, String period) {
        // Parse period (format: "YYYY-MM" or "last-month")
        LocalDate startDate, endDate;

        try {
            if ("last-month".equalsIgnoreCase(period)) {
                startDate = LocalDate.now().minusMonths(1).withDayOfMonth(1);
                endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
            } else {
                // Assume format YYYY-MM
                startDate = LocalDate.parse(period + "-01");
                endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
            }
        } catch (Exception e) {
            throw ExceptionUtils.validationError("Invalid period format. Use 'YYYY-MM' or 'last-month'");
        }

        return timesheetRepository.findByDateRange(startDate, endDate)
                .stream()
                .filter(t -> t.getPlacement().getPlacementId().equals(placement.getPlacementId()))
                .filter(t -> t.getStatus() == Timesheet.TimesheetStatus.APPROVED)
                .collect(Collectors.toList());
    }

    private InvoiceItemRequestDTO createInvoiceItemFromTimesheet(Timesheet timesheet) {
        double hours = timesheet.getTotalHours() != null ? timesheet.getTotalHours() : 0.0;
        double rate = timesheet.getPlacement().getBillingRate();

        InvoiceItemRequestDTO item = new InvoiceItemRequestDTO();
        item.setDescription(String.format("Timesheet for week %s to %s - %s hours",
                timesheet.getWeekStartDate(), timesheet.getWeekEndDate(), hours));
        item.setQuantity(1);
        item.setUnitPrice(hours * rate);
        item.setTimesheetId(timesheet.getTimesheetId());
        return item;
    }

    private void validateInvoiceDates(LocalDate invoiceDate, LocalDate dueDate) {
        if (invoiceDate.isAfter(LocalDate.now())) {
            throw ExceptionUtils.validationError("Invoice date cannot be in the future");
        }

        if (dueDate.isBefore(invoiceDate)) {
            throw ExceptionUtils.validationError("Due date cannot be before invoice date");
        }

        if (dueDate.isBefore(LocalDate.now())) {
            throw ExceptionUtils.validationError("Due date cannot be in the past");
        }
    }

    private void validateInvoiceStatusTransition(Invoice.InvoiceStatus oldStatus,
                                                 Invoice.InvoiceStatus newStatus) {
        switch (oldStatus) {
            case DRAFT:
                if (!List.of(Invoice.InvoiceStatus.PENDING_REVIEW, Invoice.InvoiceStatus.SUBMITTED).contains(newStatus)) {
                    throw ExceptionUtils.businessError("Draft invoice can only be moved to pending review or submitted");
                }
                break;
            case PENDING_REVIEW:
                if (!List.of(Invoice.InvoiceStatus.AWAITING_APPROVAL, Invoice.InvoiceStatus.SUBMITTED).contains(newStatus)) {
                    throw ExceptionUtils.businessError("Invalid status transition from PENDING_REVIEW");
                }
                break;
            case AWAITING_APPROVAL:
                if (!List.of(Invoice.InvoiceStatus.PAID).contains(newStatus)) {
                    throw ExceptionUtils.businessError("Invoice awaiting approval can only be paid");
                }
                break;
            case SUBMITTED:
                if (!List.of(Invoice.InvoiceStatus.PAID).contains(newStatus)) {
                    throw ExceptionUtils.businessError("Submitted invoice can only be paid");
                }
                break;
            case PAID:
                throw ExceptionUtils.businessError("Cannot change status of paid invoice");
            case OVERDUE:
                if (!List.of(Invoice.InvoiceStatus.PAID).contains(newStatus)) {
                    throw ExceptionUtils.businessError("Overdue invoice can only be paid");
                }
                break;
        }
    }
}