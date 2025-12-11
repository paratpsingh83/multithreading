package com.globalsoft.mg;

import com.jobportal.dto.*;
import com.jobportal.service.InterviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/interviews")
@RequiredArgsConstructor
public class InterviewController {

    private final InterviewService service;

    @PostMapping("/api/v1/create")
    public ResponseEntity<ApiResponseDTO<InterviewDTO>> create(@Valid @RequestBody InterviewRequestDTO dto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponseDTO.success(service.create(dto),"Interview scheduled successfully"));
    }

    @GetMapping("/api/v1/{id}")
    public ResponseEntity<ApiResponseDTO<InterviewDTO>> getById(@PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseDTO.success( service.getById(id),"Interview retrieved successfully"));
    }

    @GetMapping("/api/v1")
    public ResponseEntity<ApiResponseDTO<List<InterviewDTO>>> getAll() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseDTO.success( service.getAll(),"Interviews retrieved successfully"));
    }

    @PutMapping("/api/v1/update/{id}")
    public ResponseEntity<ApiResponseDTO<InterviewDTO>> update(@PathVariable Long id, @Valid @RequestBody InterviewRequestDTO dto) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseDTO.success( service.update(id, dto),"Interview updated successfully"));
    }

    @DeleteMapping("/api/v1/delete/{id}")
    public ResponseEntity<ApiResponseDTO<Void>> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseDTO.success( null,"Interview deleted successfully"));
    }

    @PutMapping("/api/v1/{id}/status")
    public ResponseEntity<ApiResponseDTO<InterviewDTO>> updateStatus(@PathVariable Long id, @Valid @RequestBody InterviewStatusDTO dto) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseDTO.success( service.updateStatus(id, dto),"Interview status updated successfully"));
    }

    @GetMapping("/api/v1/application/{applicationId}")
    public ResponseEntity<ApiResponseDTO<List<InterviewDTO>>> getByApplicationId(@PathVariable Long applicationId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseDTO.success( service.getByApplicationId(applicationId),"Application interviews retrieved successfully"));
    }
}