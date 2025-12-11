package com.jobportal.dto;

import com.jobportal.entity.TimesheetEntry;
import lombok.Data;
import java.time.LocalDate;

@Data
public class TimesheetEntryDTO {
    private Long entryId;
    private Long timesheetId;
    private LocalDate workDate;
    private Double hoursWorked;
    private String taskDescription;
    private String projectCode;
    private Boolean isBillable;
    
    public static TimesheetEntryDTO fromEntity(TimesheetEntry entry) {
        TimesheetEntryDTO dto = new TimesheetEntryDTO();
        dto.setEntryId(entry.getEntryId());
        dto.setTimesheetId(entry.getTimesheet().getTimesheetId());
        dto.setWorkDate(entry.getWorkDate());
        dto.setHoursWorked(entry.getHoursWorked());
        dto.setTaskDescription(entry.getTaskDescription());
        dto.setProjectCode(entry.getProjectCode());
        dto.setIsBillable(entry.getIsBillable());
        return dto;
    }
}