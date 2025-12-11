//package com.jobportal.entity;
//
//import jakarta.persistence.*;
//import lombok.Data;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//
//@Entity
//@Table(name = "invoices")
//@Data
//public class Invoice {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long invoiceId;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "placement_id", nullable = false)
//    private Placement placement;
//
//    @Column(unique = true, nullable = false)
//    private String invoiceNumber;
//
//    @Column(nullable = false)
//    private LocalDate invoiceDate;
//
//    @Column(nullable = false)
//    private LocalDate dueDate;
//
//    @Column(nullable = false)
//    private Double totalAmount;
//
//    private Double taxAmount;
//
//    @Enumerated(EnumType.STRING)
//    private InvoiceStatus status = InvoiceStatus.DRAFT;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "created_by", nullable = false)
//    private User createdBy;
//
//    private LocalDateTime submittedDate;
//    private LocalDate paidDate;
//    private String paymentTerms;
//
//    // One-to-Many mappings
//    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private List<InvoiceItem> items = new ArrayList<>();
//
//    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private List<Payment> payments = new ArrayList<>();
//
//    public enum InvoiceStatus {
//        DRAFT, PENDING_REVIEW, SUBMITTED, AWAITING_APPROVAL, PAID, OVERDUE
//    }
//}
package com.jobportal.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "invoices")
@Data
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long invoiceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "placement_id", nullable = false)
    private Placement placement;

    @Column(unique = true, nullable = false)
    private String invoiceNumber;

    @Column(nullable = false)
    private LocalDate invoiceDate;

    @Column(nullable = false)
    private LocalDate dueDate;

    @Column(nullable = false)
    private Double totalAmount;

    private Double taxAmount;

    @Enumerated(EnumType.STRING)
    private InvoiceStatus status = InvoiceStatus.DRAFT;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    private LocalDateTime submittedDate;
    private LocalDate paidDate;
    private String paymentTerms;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<InvoiceItem> items = new ArrayList<>();

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Payment> payments = new ArrayList<>();

//    @Transient
    private Long countByStatus;

    public enum InvoiceStatus {
        DRAFT, PENDING_REVIEW, SUBMITTED, AWAITING_APPROVAL, PAID, OVERDUE
    }
}
