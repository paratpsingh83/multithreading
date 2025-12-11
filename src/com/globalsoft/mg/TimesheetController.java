package com.globalsoft.mg;

import com.jobportal.dto.*;
import com.jobportal.service.TimesheetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/timesheets")
@RequiredArgsConstructor
public class TimesheetController {

    private final TimesheetService service;

    @PostMapping("/api/v1/create")
    public ResponseEntity<ApiResponseDTO<TimesheetDTO>> create(@Valid @RequestBody TimesheetRequestDTO dto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponseDTO.success( service.create(dto),"Timesheet created successfully"));
    }

    @GetMapping("/api/v1/{id}")
    public ResponseEntity<ApiResponseDTO<TimesheetDTO>> getById(@PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseDTO.success( service.getById(id),"Timesheet retrieved successfully"));
    }

    @GetMapping("/api/v1")
    public ResponseEntity<ApiResponseDTO<List<TimesheetDTO>>> getAll() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseDTO.success( service.getAll(),"Timesheets retrieved successfully"));
    }

    @PutMapping("/api/v1/update/{id}")
    public ResponseEntity<ApiResponseDTO<TimesheetDTO>> update(@PathVariable Long id, @Valid @RequestBody TimesheetRequestDTO dto) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseDTO.success( service.update(id, dto),"Timesheet updated successfully"));
    }

    @DeleteMapping("/api/v1/delete/{id}")
    public ResponseEntity<ApiResponseDTO<Void>> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseDTO.success( null,"Timesheet deleted successfully"));
    }

    @PutMapping("/api/v1/{id}/status")
    public ResponseEntity<ApiResponseDTO<TimesheetDTO>> updateStatus(@PathVariable Long id, @Valid @RequestBody TimesheetStatusDTO dto) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseDTO.success( service.updateStatus(id, dto),"Timesheet status updated successfully"));
    }

    @GetMapping("/api/v1/user/{userId}")
    public ResponseEntity<ApiResponseDTO<List<TimesheetDTO>>> getByUserId(@PathVariable Long userId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseDTO.success( service.getByUserId(userId),"User timesheets retrieved successfully"));
    }

    @GetMapping("/api/v1/placement/{placementId}")
    public ResponseEntity<ApiResponseDTO<List<TimesheetDTO>>> getByPlacementId(@PathVariable Long placementId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseDTO.success( service.getByPlacementId(placementId),"Placement timesheets retrieved successfully"));
    }

    @PostMapping("/api/v1/{id}/entries")
    public ResponseEntity<ApiResponseDTO<TimesheetDTO>> addEntry(@PathVariable Long id, @Valid @RequestBody TimesheetEntryRequestDTO dto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponseDTO.success( service.addEntry(id, dto),"Timesheet entry added successfully"));
    }
}