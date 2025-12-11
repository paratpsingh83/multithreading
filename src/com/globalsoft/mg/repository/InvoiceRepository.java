package com.jobportal.repository;

import com.jobportal.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    List<Invoice> findByStatus(Invoice.InvoiceStatus status);

    List<Invoice> findByPlacementPlacementId(Long placementId);

    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);


    @Query("SELECT i FROM Invoice i WHERE i.createdBy.userId = :userId")
    List<Invoice> findByCreatedBy(@Param("userId") Long userId);



    @Query("SELECT i FROM Invoice i WHERE i.dueDate < CURRENT_DATE AND i.status != 'PAID'")
    List<Invoice> findOverdueInvoices();





    @Query("SELECT COUNT(i) FROM Invoice i WHERE i.status = 'PENDING_REVIEW'")
    Long countPendingReview();

    @Query("SELECT COUNT(i) FROM Invoice i WHERE i.status = 'SUBMITTED'")
    Long countSubmitted();

    @Query("SELECT COUNT(i) FROM Invoice i WHERE i.status = 'AWAITING_APPROVAL'")
    Long countAwaitingApproval();

    @Query("SELECT COUNT(i) FROM Invoice i WHERE i.status = :status")
    Long countByStatus(@Param("status") Invoice.InvoiceStatus status);

    @Query("SELECT SUM(i.totalAmount) FROM Invoice i WHERE i.status IN ('PENDING', 'OVERDUE')")
    Double sumOutstandingAmount();

//    @Query("SELECT SUM(i.totalAmount) FROM Invoice i WHERE i.status = 'PAID' AND i.paymentDate >= :startDate")
//    Double sumPaidAmountSince(@Param("startDate") LocalDate startDate);


    // Alternative: Use invoice date instead of payment date
    @Query("SELECT SUM(i.totalAmount) FROM Invoice i WHERE i.status = 'PAID' AND i.invoiceDate >= :startDate")
    Double sumPaidInvoicesSince(@Param("startDate") LocalDate startDate);

}