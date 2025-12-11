package com.jobportal.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "timesheet_entries")
@Data
public class TimesheetEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long entryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "timesheet_id", nullable = false)
    private Timesheet timesheet;

    @Column(nullable = false)
    private LocalDate workDate;

    @Column(nullable = false)
    private Double hoursWorked;

    private String taskDescription;
    private String projectCode;
    private Boolean isBillable = true;
}