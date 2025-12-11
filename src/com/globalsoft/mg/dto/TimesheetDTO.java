package com.jobportal.dto;

import com.jobportal.entity.Timesheet;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class TimesheetDTO {
    private Long timesheetId;
    private PlacementDTO placement;
    private UserDTO consultant;
    private LocalDate weekStartDate;
    private LocalDate weekEndDate;
    private Double totalHours;
    private Timesheet.TimesheetStatus status;
    private LocalDateTime submittedDate;
    private LocalDateTime approvedDate;
    private UserDTO approvedBy;
    private String rejectionReason;
    private List<TimesheetEntryDTO> entries;
    
    public static TimesheetDTO fromEntity(Timesheet timesheet) {
        TimesheetDTO dto = new TimesheetDTO();
        dto.setTimesheetId(timesheet.getTimesheetId());
        dto.setPlacement(PlacementDTO.fromEntity(timesheet.getPlacement()));
        dto.setConsultant(UserDTO.fromEntity(timesheet.getConsultant()));
        dto.setWeekStartDate(timesheet.getWeekStartDate());
        dto.setWeekEndDate(timesheet.getWeekEndDate());
        dto.setTotalHours(timesheet.getTotalHours());
        dto.setStatus(timesheet.getStatus());
        dto.setSubmittedDate(timesheet.getSubmittedDate());
        dto.setApprovedDate(timesheet.getApprovedDate());
        
        if (timesheet.getApprovedBy() != null) {
            dto.setApprovedBy(UserDTO.fromEntity(timesheet.getApprovedBy()));
        }
        
        dto.setRejectionReason(timesheet.getRejectionReason());
        return dto;
    }
}