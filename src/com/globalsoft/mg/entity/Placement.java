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
//@Table(name = "placements")
//@Data
//public class Placement {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long placementId;
//
//    @OneToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "application_id", nullable = false)
//    private Application application;
//
//    private LocalDate startDate;
//    private LocalDate endDate;
//
//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
//    private ContractType contractType;
//
//    private Double billingRate;
//    private Double clientBillRate;
//
//    @Enumerated(EnumType.STRING)
//    private PlacementStatus placementStatus = PlacementStatus.ACTIVE;
//
//    private LocalDateTime createdAt;
//    private LocalDateTime updatedAt;
//
//    // One-to-Many mappings
//    @OneToMany(mappedBy = "placement", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private List<Timesheet> timesheets = new ArrayList<>();
//
//    @OneToMany(mappedBy = "placement", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private List<Invoice> invoices = new ArrayList<>();
//
//    @PrePersist
//    protected void onCreate() {
//        createdAt = LocalDateTime.now();
//        updatedAt = LocalDateTime.now();
//    }
//
//    @PreUpdate
//    protected void onUpdate() {
//        updatedAt = LocalDateTime.now();
//    }
//
//    public enum ContractType {
//        FULL_TIME, CONTRACT
//    }
//
//    public enum PlacementStatus {
//        ACTIVE, COMPLETED, TERMINATED
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
@Table(name = "placements")
@Data
public class Placement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long placementId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private Application application;

    private LocalDate startDate;
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContractType contractType;

    private Double billingRate;
    private Double clientBillRate;

    @Enumerated(EnumType.STRING)
    private PlacementStatus placementStatus = PlacementStatus.ACTIVE;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "placement", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Timesheet> timesheets = new ArrayList<>();

    @OneToMany(mappedBy = "placement", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Invoice> invoices = new ArrayList<>();

//    @Transient
    private Long countByStatus;  // <-- Added field

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum ContractType {
        FULL_TIME, CONTRACT
    }

    public enum PlacementStatus {
        ACTIVE, COMPLETED, TERMINATED
    }
}
