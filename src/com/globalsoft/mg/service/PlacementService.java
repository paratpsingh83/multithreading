package com.jobportal.service;

import com.jobportal.dto.PlacementDTO;
import com.jobportal.dto.PlacementRequestDTO;
import com.jobportal.dto.PlacementStatsDTO;

import java.util.List;

public interface PlacementService {
    PlacementDTO create(PlacementRequestDTO placementRequestDTO);

    PlacementDTO getById(Long id);

    List<PlacementDTO> getAll();

    PlacementDTO update(Long id, PlacementRequestDTO placementRequestDTO);

    void delete(Long id);

    PlacementStatsDTO getOverview();

    PlacementDTO updateStatus(Long id, String status);

    PlacementStatsDTO getStats();

    List<PlacementDTO> getActivePlacements();

    List<PlacementDTO> getByConsultantId(Long consultantId);
}