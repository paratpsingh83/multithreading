package com.jobportal.serviceImpl;

import com.jobportal.dto.PlacementDTO;
import com.jobportal.dto.PlacementRequestDTO;
import com.jobportal.dto.PlacementStatsDTO;
import com.jobportal.entity.Application;
import com.jobportal.entity.Placement;
import com.jobportal.exception.ExceptionUtils;
import com.jobportal.repository.ApplicationRepository;
import com.jobportal.repository.PlacementRepository;
import com.jobportal.service.NotificationService;
import com.jobportal.service.PlacementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlacementServiceImpl implements PlacementService {

    private final PlacementRepository placementRepository;
    private final ApplicationRepository applicationRepository;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public PlacementDTO create(PlacementRequestDTO placementRequestDTO) {
        Application application = applicationRepository.findById(placementRequestDTO.getApplicationId())
                .orElseThrow(() -> ExceptionUtils.resourceNotFound("Application", "id", placementRequestDTO.getApplicationId()));

        // Validate application is in HIRED status
        if (application.getStatus() != Application.ApplicationStatus.HIRED) {
            throw ExceptionUtils.businessError("Cannot create placement for non-hired application");
        }

        // Check if placement already exists for this application
        if (application.getPlacement() != null) {
            throw ExceptionUtils.duplicateResource("Placement", "application", placementRequestDTO.getApplicationId());
        }

        // Validate dates
        validatePlacementDates(placementRequestDTO.getStartDate(), placementRequestDTO.getEndDate());

        // Validate billing rates
        validateBillingRates(placementRequestDTO.getBillingRate(), placementRequestDTO.getClientBillRate());

        Placement placement = new Placement();
        placement.setApplication(application);
        placement.setStartDate(placementRequestDTO.getStartDate());
        placement.setEndDate(placementRequestDTO.getEndDate());
        placement.setContractType(placementRequestDTO.getContractType());
        placement.setBillingRate(placementRequestDTO.getBillingRate());
        placement.setClientBillRate(placementRequestDTO.getClientBillRate());
        placement.setPlacementStatus(placementRequestDTO.getPlacementStatus());

        Placement savedPlacement = placementRepository.save(placement);

        // Update application to reflect placement
        application.setPlacement(savedPlacement);
        applicationRepository.save(application);

        // Notify consultant and client
        String notificationMessage = String.format(
                "Congratulations! You have been placed for '%s' starting %s. Contract Type: %s",
                application.getJob().getJobTitle(),
                placementRequestDTO.getStartDate(),
                placementRequestDTO.getContractType()
        );

        notificationService.createNotification(
                application.getConsultant().getUserId(),
                "Placement Created",
                notificationMessage,
                "PLACEMENT",
                savedPlacement.getPlacementId()
        );

        return PlacementDTO.fromEntity(savedPlacement);
    }

    @Override
    public PlacementDTO getById(Long id) {
        Placement placement = placementRepository.findById(id)
                .orElseThrow(() -> ExceptionUtils.resourceNotFound("Placement", "id", id));
        return PlacementDTO.fromEntity(placement);
    }

    @Override
    public List<PlacementDTO> getAll() {
        return placementRepository.findAll()
                .stream()
                .map(PlacementDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PlacementDTO update(Long id, PlacementRequestDTO placementRequestDTO) {
        Placement placement = placementRepository.findById(id)
                .orElseThrow(() -> ExceptionUtils.resourceNotFound("Placement", "id", id));

        // Validate placement is active for updates
        if (placement.getPlacementStatus() != Placement.PlacementStatus.ACTIVE) {
            throw ExceptionUtils.businessError("Cannot update completed or terminated placement");
        }

        // Update allowed fields with validation
        if (placementRequestDTO.getEndDate() != null) {
            validateEndDate(placement.getStartDate(), placementRequestDTO.getEndDate());
            placement.setEndDate(placementRequestDTO.getEndDate());
        }

        if (placementRequestDTO.getBillingRate() != null) {
            placement.setBillingRate(placementRequestDTO.getBillingRate());
        }

        if (placementRequestDTO.getClientBillRate() != null) {
            placement.setClientBillRate(placementRequestDTO.getClientBillRate());
        }

        // Validate billing rates after update
        validateBillingRates(placement.getBillingRate(), placement.getClientBillRate());

        Placement updatedPlacement = placementRepository.save(placement);
        return PlacementDTO.fromEntity(updatedPlacement);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Placement placement = placementRepository.findById(id)
                .orElseThrow(() -> ExceptionUtils.resourceNotFound("Placement", "id", id));

        if (placement.getPlacementStatus() == Placement.PlacementStatus.ACTIVE) {
            throw ExceptionUtils.businessError("Cannot delete active placement");
        }

        // Remove placement reference from application
        Application application = placement.getApplication();
        application.setPlacement(null);
        applicationRepository.save(application);

        placementRepository.delete(placement);
    }

    @Override
    public PlacementStatsDTO getOverview() {
        PlacementStatsDTO stats = new PlacementStatsDTO();

        // Placement by type
        Map<String, Long> placementsByType = new HashMap<>();
        placementsByType.put("FULL_TIME", placementRepository.countFullTimePlacements());
        placementsByType.put("CONTRACT", placementRepository.countContractPlacements());
        stats.setPlacementsByType(placementsByType);

        // Placement by status
        Map<String, Long> placementsByStatus = new HashMap<>();
        for (Placement.PlacementStatus status : Placement.PlacementStatus.values()) {
            placementsByStatus.put(status.name(), placementRepository.countByPlacementStatus(status));
        }
        stats.setPlacementsByStatus(placementsByStatus);

        stats.setTotalPlacements(placementRepository.count());
        stats.setActivePlacements(placementRepository.countActivePlacements());
        stats.setNewPlacementsThisMonth(placementRepository.countPlacementsSince(LocalDate.now().withDayOfMonth(1)));

        return stats;
    }

    @Override
    @Transactional
    public PlacementDTO updateStatus(Long id, String status) {
        Placement placement = placementRepository.findById(id)
                .orElseThrow(() -> ExceptionUtils.resourceNotFound("Placement", "id", id));

        try {
            Placement.PlacementStatus newStatus = Placement.PlacementStatus.valueOf(status.toUpperCase());
            Placement.PlacementStatus oldStatus = placement.getPlacementStatus();

            // Validate status transition
            validatePlacementStatusTransition(oldStatus, newStatus);

            placement.setPlacementStatus(newStatus);
            Placement updatedPlacement = placementRepository.save(placement);

            // Notify about status change
            String notificationMessage = String.format(
                    "Placement status changed from %s to %s for '%s'",
                    oldStatus, newStatus, placement.getApplication().getJob().getJobTitle()
            );

            notificationService.createNotification(
                    placement.getApplication().getConsultant().getUserId(),
                    "Placement Status Updated",
                    notificationMessage,
                    "PLACEMENT",
                    updatedPlacement.getPlacementId()
            );

            return PlacementDTO.fromEntity(updatedPlacement);
        } catch (IllegalArgumentException e) {
            throw ExceptionUtils.validationError("Invalid placement status: " + status);
        }
    }

    @Override
    public PlacementStatsDTO getStats() {
        return getOverview();
    }

    @Override
    public List<PlacementDTO> getActivePlacements() {
        return placementRepository.findActivePlacements()
                .stream()
                .map(PlacementDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<PlacementDTO> getByConsultantId(Long consultantId) {
        return placementRepository.findByConsultantId(consultantId)
                .stream()
                .map(PlacementDTO::fromEntity)
                .collect(Collectors.toList());
    }

    private void validatePlacementDates(LocalDate startDate, LocalDate endDate) {
        if (startDate.isBefore(LocalDate.now())) {
            throw ExceptionUtils.validationError("Start date cannot be in the past");
        }

        if (endDate != null && endDate.isBefore(startDate)) {
            throw ExceptionUtils.validationError("End date cannot be before start date");
        }
    }

    private void validateEndDate(LocalDate startDate, LocalDate endDate) {
        if (endDate != null && endDate.isBefore(startDate)) {
            throw ExceptionUtils.validationError("End date cannot be before start date");
        }
    }

    private void validateBillingRates(Double billingRate, Double clientBillRate) {
        if (billingRate == null || billingRate <= 0) {
            throw ExceptionUtils.validationError("Billing rate must be greater than 0");
        }

        if (clientBillRate != null && clientBillRate < billingRate) {
            throw ExceptionUtils.validationError("Client bill rate cannot be less than billing rate");
        }
    }

    private void validatePlacementStatusTransition(Placement.PlacementStatus oldStatus,
                                                   Placement.PlacementStatus newStatus) {
        if (oldStatus == Placement.PlacementStatus.COMPLETED || oldStatus == Placement.PlacementStatus.TERMINATED) {
            throw ExceptionUtils.businessError("Cannot change status of completed or terminated placement");
        }

        if (oldStatus == newStatus) {
            throw ExceptionUtils.businessError("Placement is already in " + newStatus + " status");
        }
    }
}