package com.jobportal.serviceImpl;

import com.jobportal.dto.*;
import com.jobportal.entity.Placement;
import com.jobportal.entity.Timesheet;
import com.jobportal.entity.TimesheetEntry;
import com.jobportal.entity.User;
import com.jobportal.exception.ExceptionUtils;
import com.jobportal.repository.PlacementRepository;
import com.jobportal.repository.TimesheetEntryRepository;
import com.jobportal.repository.TimesheetRepository;
import com.jobportal.repository.UserRepository;
import com.jobportal.service.NotificationService;
import com.jobportal.service.TimesheetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TimesheetServiceImpl implements TimesheetService {

    private final TimesheetRepository timesheetRepository;
    private final TimesheetEntryRepository timesheetEntryRepository;
    private final PlacementRepository placementRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public TimesheetDTO create(TimesheetRequestDTO timesheetRequestDTO) {
        Placement placement = placementRepository.findById(timesheetRequestDTO.getPlacementId())
                .orElseThrow(() -> ExceptionUtils.resourceNotFound("Placement", "id", timesheetRequestDTO.getPlacementId()));

        // Validate placement is active
        if (placement.getPlacementStatus() != Placement.PlacementStatus.ACTIVE) {
            throw ExceptionUtils.businessError("Cannot create timesheet for inactive placement");
        }

        // Validate week dates
        validateWeekDates(timesheetRequestDTO.getWeekStartDate(), timesheetRequestDTO.getWeekEndDate());

        // Check if timesheet already exists for this week
        User consultant = placement.getApplication().getConsultant();
        if (timesheetRepository.findByConsultantAndWeek(consultant.getUserId(), timesheetRequestDTO.getWeekStartDate()).isPresent()) {
            throw ExceptionUtils.duplicateResource("Timesheet", "consultant and week",
                    "Consultant: " + consultant.getUserId() + ", Week: " + timesheetRequestDTO.getWeekStartDate());
        }

        Timesheet timesheet = new Timesheet();
        timesheet.setPlacement(placement);
        timesheet.setConsultant(consultant);
        timesheet.setWeekStartDate(timesheetRequestDTO.getWeekStartDate());
        timesheet.setWeekEndDate(timesheetRequestDTO.getWeekEndDate());
        timesheet.setStatus(Timesheet.TimesheetStatus.DRAFT);
        timesheet.setTotalHours(0.0);

        Timesheet savedTimesheet = timesheetRepository.save(timesheet);

        // Add entries if provided
        if (timesheetRequestDTO.getEntries() != null && !timesheetRequestDTO.getEntries().isEmpty()) {
            for (TimesheetEntryRequestDTO entryDTO : timesheetRequestDTO.getEntries()) {
                addEntryToTimesheet(savedTimesheet, entryDTO);
            }
            // Recalculate total hours after adding entries
            updateTimesheetTotalHours(savedTimesheet);
        }

        return TimesheetDTO.fromEntity(savedTimesheet);
    }

    @Override
    public TimesheetDTO getById(Long id) {
        Timesheet timesheet = timesheetRepository.findById(id)
                .orElseThrow(() -> ExceptionUtils.resourceNotFound("Timesheet", "id", id));

        // Load entries eagerly for the DTO
        timesheet.setEntries(timesheetEntryRepository.findByTimesheetTimesheetId(id));

        return TimesheetDTO.fromEntity(timesheet);
    }

    @Override
    public List<TimesheetDTO> getAll() {
        return timesheetRepository.findAll()
                .stream()
                .map(timesheet -> {
                    // Load entries for each timesheet
                    timesheet.setEntries(timesheetEntryRepository.findByTimesheetTimesheetId(timesheet.getTimesheetId()));
                    return TimesheetDTO.fromEntity(timesheet);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TimesheetDTO update(Long id, TimesheetRequestDTO timesheetRequestDTO) {
        Timesheet timesheet = timesheetRepository.findById(id)
                .orElseThrow(() -> ExceptionUtils.resourceNotFound("Timesheet", "id", id));

        // Only allow updates to draft timesheets
        if (timesheet.getStatus() != Timesheet.TimesheetStatus.DRAFT) {
            throw ExceptionUtils.businessError("Cannot update submitted or approved timesheet");
        }

        // Update week dates if provided
        if (timesheetRequestDTO.getWeekStartDate() != null) {
            timesheet.setWeekStartDate(timesheetRequestDTO.getWeekStartDate());
        }
        if (timesheetRequestDTO.getWeekEndDate() != null) {
            timesheet.setWeekEndDate(timesheetRequestDTO.getWeekEndDate());
        }

        // Validate week dates
        validateWeekDates(timesheet.getWeekStartDate(), timesheet.getWeekEndDate());

        Timesheet updatedTimesheet = timesheetRepository.save(timesheet);
        return TimesheetDTO.fromEntity(updatedTimesheet);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Timesheet timesheet = timesheetRepository.findById(id)
                .orElseThrow(() -> ExceptionUtils.resourceNotFound("Timesheet", "id", id));

        if (timesheet.getStatus() != Timesheet.TimesheetStatus.DRAFT) {
            throw ExceptionUtils.businessError("Cannot delete submitted or approved timesheet");
        }

        // Delete all entries first
        timesheetEntryRepository.deleteAll(timesheet.getEntries());
        timesheetRepository.delete(timesheet);
    }

    @Override
    @Transactional
    public TimesheetDTO updateStatus(Long id, TimesheetStatusDTO statusDTO) {
        Timesheet timesheet = timesheetRepository.findById(id)
                .orElseThrow(() -> ExceptionUtils.resourceNotFound("Timesheet", "id", id));

        Timesheet.TimesheetStatus newStatus = statusDTO.getStatus();
        Timesheet.TimesheetStatus oldStatus = timesheet.getStatus();

        // Validate status transition
        validateTimesheetStatusTransition(oldStatus, newStatus);

        timesheet.setStatus(newStatus);

        if (newStatus == Timesheet.TimesheetStatus.SUBMITTED) {
            timesheet.setSubmittedDate(java.time.LocalDateTime.now());
        } else if (newStatus == Timesheet.TimesheetStatus.APPROVED) {
            timesheet.setApprovedDate(java.time.LocalDateTime.now());
            // In real app, set approvedBy from security context
            timesheet.setApprovedBy(userRepository.findById(1L).orElse(null));
        } else if (newStatus == Timesheet.TimesheetStatus.REJECTED) {
            timesheet.setRejectionReason(statusDTO.getRejectionReason());
        }

        Timesheet updatedTimesheet = timesheetRepository.save(timesheet);

        // Notify about status change
        String notificationMessage = String.format(
                "Timesheet for week %s to %s has been %s",
                timesheet.getWeekStartDate(), timesheet.getWeekEndDate(), newStatus
        );

        if (newStatus == Timesheet.TimesheetStatus.REJECTED && statusDTO.getRejectionReason() != null) {
            notificationMessage += ". Reason: " + statusDTO.getRejectionReason();
        }

        notificationService.createNotification(
                timesheet.getConsultant().getUserId(),
                "Timesheet Status Updated",
                notificationMessage,
                "TIMESHEET",
                updatedTimesheet.getTimesheetId()
        );

        return TimesheetDTO.fromEntity(updatedTimesheet);
    }

    @Override
    public List<TimesheetDTO> getByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw ExceptionUtils.resourceNotFound("User", "id", userId);
        }

        return timesheetRepository.findByConsultantUserId(userId)
                .stream()
                .map(timesheet -> {
                    timesheet.setEntries(timesheetEntryRepository.findByTimesheetTimesheetId(timesheet.getTimesheetId()));
                    return TimesheetDTO.fromEntity(timesheet);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<TimesheetDTO> getByPlacementId(Long placementId) {
        if (!placementRepository.existsById(placementId)) {
            throw ExceptionUtils.resourceNotFound("Placement", "id", placementId);
        }

        return timesheetRepository.findByPlacementPlacementId(placementId)
                .stream()
                .map(timesheet -> {
                    timesheet.setEntries(timesheetEntryRepository.findByTimesheetTimesheetId(timesheet.getTimesheetId()));
                    return TimesheetDTO.fromEntity(timesheet);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TimesheetDTO addEntry(Long timesheetId, TimesheetEntryRequestDTO entryDTO) {
        Timesheet timesheet = timesheetRepository.findById(timesheetId)
                .orElseThrow(() -> ExceptionUtils.resourceNotFound("Timesheet", "id", timesheetId));

        // Only allow adding entries to draft timesheets
        if (timesheet.getStatus() != Timesheet.TimesheetStatus.DRAFT) {
            throw ExceptionUtils.businessError("Cannot add entries to submitted or approved timesheet");
        }

        addEntryToTimesheet(timesheet, entryDTO);
        updateTimesheetTotalHours(timesheet);

        Timesheet updatedTimesheet = timesheetRepository.save(timesheet);
        return TimesheetDTO.fromEntity(updatedTimesheet);
    }

    @Override
    @Transactional
    public TimesheetDTO updateEntry(Long timesheetId, Long entryId, TimesheetEntryRequestDTO entryDTO) {
        Timesheet timesheet = timesheetRepository.findById(timesheetId)
                .orElseThrow(() -> ExceptionUtils.resourceNotFound("Timesheet", "id", timesheetId));

        if (timesheet.getStatus() != Timesheet.TimesheetStatus.DRAFT) {
            throw ExceptionUtils.businessError("Cannot update entries in submitted or approved timesheet");
        }

        TimesheetEntry entry = timesheetEntryRepository.findById(entryId)
                .orElseThrow(() -> ExceptionUtils.resourceNotFound("TimesheetEntry", "id", entryId));

        // Validate entry belongs to timesheet
        if (!entry.getTimesheet().getTimesheetId().equals(timesheetId)) {
            throw ExceptionUtils.businessError("Entry does not belong to the specified timesheet");
        }

        // Validate work date is within timesheet week
        if (entryDTO.getWorkDate().isBefore(timesheet.getWeekStartDate()) ||
                entryDTO.getWorkDate().isAfter(timesheet.getWeekEndDate())) {
            throw ExceptionUtils.validationError("Work date must be within the timesheet week");
        }

        // Validate hours
        if (entryDTO.getHoursWorked() <= 0 || entryDTO.getHoursWorked() > 24) {
            throw ExceptionUtils.validationError("Hours worked must be between 0 and 24");
        }

        // Update entry
        entry.setWorkDate(entryDTO.getWorkDate());
        entry.setHoursWorked(entryDTO.getHoursWorked());
        entry.setTaskDescription(entryDTO.getTaskDescription());
        entry.setProjectCode(entryDTO.getProjectCode());
        entry.setIsBillable(entryDTO.getIsBillable());

        timesheetEntryRepository.save(entry);
        updateTimesheetTotalHours(timesheet);

        Timesheet updatedTimesheet = timesheetRepository.save(timesheet);
        return TimesheetDTO.fromEntity(updatedTimesheet);
    }

    @Override
    @Transactional
    public void deleteEntry(Long timesheetId, Long entryId) {
        Timesheet timesheet = timesheetRepository.findById(timesheetId)
                .orElseThrow(() -> ExceptionUtils.resourceNotFound("Timesheet", "id", timesheetId));

        if (timesheet.getStatus() != Timesheet.TimesheetStatus.DRAFT) {
            throw ExceptionUtils.businessError("Cannot delete entries from submitted or approved timesheet");
        }

        TimesheetEntry entry = timesheetEntryRepository.findById(entryId)
                .orElseThrow(() -> ExceptionUtils.resourceNotFound("TimesheetEntry", "id", entryId));

        if (!entry.getTimesheet().getTimesheetId().equals(timesheetId)) {
            throw ExceptionUtils.businessError("Entry does not belong to the specified timesheet");
        }

        timesheetEntryRepository.delete(entry);
        updateTimesheetTotalHours(timesheet);
        timesheetRepository.save(timesheet);
    }

    @Override
    public TimesheetStatsDTO getTimesheetStats() {
        TimesheetStatsDTO stats = new TimesheetStatsDTO();

        Map<String, Long> statusCounts = new HashMap<>();
        for (Timesheet.TimesheetStatus status : Timesheet.TimesheetStatus.values()) {
            statusCounts.put(status.name(), timesheetRepository.countByStatus(status));
        }

        stats.setStatusCounts(statusCounts);
        stats.setTotalTimesheets(timesheetRepository.count());
        stats.setPendingReviewCount(timesheetRepository.countPendingReview());
        stats.setSubmittedCount(timesheetRepository.countSubmitted());
        stats.setAwaitingApprovalCount(timesheetRepository.countByStatus(Timesheet.TimesheetStatus.PENDING_REVIEW));
        stats.setTotalHoursThisMonth(timesheetRepository.sumApprovedHoursSince(LocalDate.now().withDayOfMonth(1)));

        return stats;
    }

    private void addEntryToTimesheet(Timesheet timesheet, TimesheetEntryRequestDTO entryDTO) {
        // Validate work date is within timesheet week
        if (entryDTO.getWorkDate().isBefore(timesheet.getWeekStartDate()) ||
                entryDTO.getWorkDate().isAfter(timesheet.getWeekEndDate())) {
            throw ExceptionUtils.validationError("Work date must be within the timesheet week");
        }

        // Validate hours
        if (entryDTO.getHoursWorked() <= 0 || entryDTO.getHoursWorked() > 24) {
            throw ExceptionUtils.validationError("Hours worked must be between 0 and 24");
        }

        TimesheetEntry entry = new TimesheetEntry();
        entry.setTimesheet(timesheet);
        entry.setWorkDate(entryDTO.getWorkDate());
        entry.setHoursWorked(entryDTO.getHoursWorked());
        entry.setTaskDescription(entryDTO.getTaskDescription());
        entry.setProjectCode(entryDTO.getProjectCode());
        entry.setIsBillable(entryDTO.getIsBillable() != null ? entryDTO.getIsBillable() : true);

        timesheetEntryRepository.save(entry);
    }

    private void updateTimesheetTotalHours(Timesheet timesheet) {
        Double totalHours = timesheetEntryRepository.findByTimesheetTimesheetId(timesheet.getTimesheetId())
                .stream()
                .mapToDouble(TimesheetEntry::getHoursWorked)
                .sum();
        timesheet.setTotalHours(totalHours);
    }

    private void validateWeekDates(LocalDate weekStartDate, LocalDate weekEndDate) {
        if (weekStartDate.getDayOfWeek() != DayOfWeek.MONDAY) {
            throw ExceptionUtils.validationError("Week start date must be a Monday");
        }

        if (weekEndDate.getDayOfWeek() != DayOfWeek.SUNDAY) {
            throw ExceptionUtils.validationError("Week end date must be a Sunday");
        }

        if (!weekEndDate.equals(weekStartDate.plusDays(6))) {
            throw ExceptionUtils.validationError("Week must be exactly 7 days");
        }

        // Ensure week is not in the future
        if (weekStartDate.isAfter(LocalDate.now())) {
            throw ExceptionUtils.validationError("Cannot create timesheet for future weeks");
        }
    }

    private void validateTimesheetStatusTransition(Timesheet.TimesheetStatus oldStatus,
                                                   Timesheet.TimesheetStatus newStatus) {
        switch (oldStatus) {
            case DRAFT:
                if (newStatus != Timesheet.TimesheetStatus.SUBMITTED) {
                    throw ExceptionUtils.businessError("Draft timesheet can only be submitted");
                }
                break;
            case SUBMITTED:
                if (!List.of(Timesheet.TimesheetStatus.PENDING_REVIEW, Timesheet.TimesheetStatus.APPROVED,
                        Timesheet.TimesheetStatus.REJECTED).contains(newStatus)) {
                    throw ExceptionUtils.businessError("Invalid status transition from SUBMITTED");
                }
                break;
            case PENDING_REVIEW:
                if (!List.of(Timesheet.TimesheetStatus.APPROVED, Timesheet.TimesheetStatus.REJECTED).contains(newStatus)) {
                    throw ExceptionUtils.businessError("Invalid status transition from PENDING_REVIEW");
                }
                break;
            case APPROVED:
            case REJECTED:
                throw ExceptionUtils.businessError("Cannot change status from " + oldStatus);
        }
    }
}