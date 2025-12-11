package com.globalsoft.mg;

import com.jobportal.dto.*;
import com.jobportal.service.PlacementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/placements")
@RequiredArgsConstructor
public class PlacementController {

    private final PlacementService service;

    @PostMapping("/api/v1/create")
    public ResponseEntity<ApiResponseDTO<PlacementDTO>> create(@Valid @RequestBody PlacementRequestDTO dto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponseDTO.success( service.create(dto),"Placement created successfully"));
    }

    @GetMapping("/api/v1/{id}")
    public ResponseEntity<ApiResponseDTO<PlacementDTO>> getById(@PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseDTO.success( service.getById(id),"Placement retrieved successfully"));
    }

    @GetMapping("/api/v1")
    public ResponseEntity<ApiResponseDTO<List<PlacementDTO>>> getAll() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseDTO.success( service.getAll(),"Placements retrieved successfully"));
    }

    @PutMapping("/api/v1/update/{id}")
    public ResponseEntity<ApiResponseDTO<PlacementDTO>> update(@PathVariable Long id, @Valid @RequestBody PlacementRequestDTO dto) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseDTO.success( service.update(id, dto),"Placement updated successfully"));
    }

    @DeleteMapping("/api/v1/delete/{id}")
    public ResponseEntity<ApiResponseDTO<Void>> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseDTO.success( null,"Placement deleted successfully"));
    }

    @GetMapping("/api/v1/overview")
    public ResponseEntity<ApiResponseDTO<PlacementStatsDTO>> getOverview() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseDTO.success( service.getOverview(),"Placement overview retrieved successfully"));
    }

    @PutMapping("/api/v1/{id}/status")
    public ResponseEntity<ApiResponseDTO<PlacementDTO>> updateStatus(@PathVariable Long id, @RequestParam String status) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseDTO.success(service.updateStatus(id, status),"Placement status updated successfully"));
    }

    @GetMapping("/api/v1/stats")
    public ResponseEntity<ApiResponseDTO<PlacementStatsDTO>> getStats() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseDTO.success( service.getStats(),"Placement stats retrieved successfully"));
    }
}