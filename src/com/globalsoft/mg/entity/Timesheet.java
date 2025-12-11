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
//@Table(name = "timesheets")
//@Data
//public class Timesheet {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long timesheetId;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "placement_id", nullable = false)
//    private Placement placement;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "consultant_id", nullable = false)
//    private User consultant;
//
//    @Column(nullable = false)
//    private LocalDate weekStartDate;
//
//    @Column(nullable = false)
//    private LocalDate weekEndDate;
//
//    private Double totalHours;
//
//    @Enumerated(EnumType.STRING)
//    private TimesheetStatus status = TimesheetStatus.DRAFT;
//
//    private LocalDateTime submittedDate;
//    private LocalDateTime approvedDate;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "approved_by")
//    private User approvedBy;
//
//    private String rejectionReason;
//
//    // One-to-Many mappings
//    @OneToMany(mappedBy = "timesheet", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private List<TimesheetEntry> entries = new ArrayList<>();
//
//    @OneToMany(mappedBy = "timesheet", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private List<InvoiceItem> invoiceItems = new ArrayList<>();
//
//    public enum TimesheetStatus {
//        DRAFT, SUBMITTED, PENDING_REVIEW, APPROVED, REJECTED
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
@Table(name = "timesheets")
@Data
public class Timesheet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long timesheetId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "placement_id", nullable = false)
    private Placement placement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consultant_id", nullable = false)
    private User consultant;

    @Column(nullable = false)
    private LocalDate weekStartDate;

    @Column(nullable = false)
    private LocalDate weekEndDate;

    private Double totalHours;

    @Enumerated(EnumType.STRING)
    private TimesheetStatus status = TimesheetStatus.DRAFT;

    private LocalDateTime submittedDate;
    private LocalDateTime approvedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private User approvedBy;

    private String rejectionReason;

    @OneToMany(mappedBy = "timesheet", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TimesheetEntry> entries = new ArrayList<>();

    @OneToMany(mappedBy = "timesheet", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<InvoiceItem> invoiceItems = new ArrayList<>();

    //    @Transient
    private Long countByStatus; // <-- Added field

    public enum TimesheetStatus {
        DRAFT, SUBMITTED, PENDING_REVIEW, APPROVED, REJECTED
    }
}
