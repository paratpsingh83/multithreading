package com.jobportal.repository;

import com.jobportal.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByInvoiceInvoiceId(Long invoiceId);

    @Query("SELECT p FROM Payment p WHERE p.processedBy.userId = :userId")
    List<Payment> findByProcessedBy(@Param("userId") Long userId);

    @Query("SELECT p FROM Payment p WHERE p.paymentDate BETWEEN :startDate AND :endDate")
    List<Payment> findByPaymentDateRange(@Param("startDate") LocalDate startDate,
                                         @Param("endDate") LocalDate endDate);
    @Query("SELECT COUNT(p) FROM Placement p WHERE p.endDate > CURRENT_DATE OR p.endDate IS NULL")
    long countActivePlacements();



    @Query("SELECT SUM(p.amountPaid) FROM Payment p WHERE p.paymentDate BETWEEN :startDate AND :endDate")
    Double sumPaymentsBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);



    @Query("SELECT SUM(p.amountPaid) FROM Payment p WHERE p.paymentDate >= :startDate")
    Double sumPaymentsSince(@Param("startDate") LocalDate startDate);

    // Add other payment methods if needed
    @Query("SELECT COUNT(p.status) FROM Payment p WHERE p.status = 'PENDING'")
    Long countPendingPayments();

}


