package com.jobportal.service;

import com.jobportal.dto.*;

import java.util.List;

public interface TimesheetService {
    TimesheetDTO create(TimesheetRequestDTO timesheetRequestDTO);

    TimesheetDTO getById(Long id);

    List<TimesheetDTO> getAll();

    TimesheetDTO update(Long id, TimesheetRequestDTO timesheetRequestDTO);

    void delete(Long id);

    TimesheetDTO updateStatus(Long id, TimesheetStatusDTO statusDTO);

    List<TimesheetDTO> getByUserId(Long userId);

    List<TimesheetDTO> getByPlacementId(Long placementId);

    TimesheetDTO addEntry(Long timesheetId, TimesheetEntryRequestDTO entryDTO);

    TimesheetDTO updateEntry(Long timesheetId, Long entryId, TimesheetEntryRequestDTO entryDTO);

    void deleteEntry(Long timesheetId, Long entryId);

    TimesheetStatsDTO getTimesheetStats();
}