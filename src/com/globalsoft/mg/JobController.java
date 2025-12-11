package com.globalsoft.mg;

import com.jobportal.dto.*;
import com.jobportal.service.JobService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService service;

    @PostMapping("/api/v1/create")
    public ResponseEntity<ApiResponseDTO<JobDTO>> create(@Valid @RequestBody JobRequestDTO dto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponseDTO.success( service.create(dto),"Job created successfully"));
    }

    @GetMapping("/api/v1/{id}")
    public ResponseEntity<ApiResponseDTO<JobDTO>> getById(@PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseDTO.success( service.getById(id),"Job retrieved successfully"));
    }

    @GetMapping("/api/v1")
    public ResponseEntity<ApiResponseDTO<List<JobDTO>>> getAll() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseDTO.success( service.getAll(),"Jobs retrieved successfully"));
    }

    @PutMapping("/api/v1/update/{id}")
    public ResponseEntity<ApiResponseDTO<JobDTO>> update(@PathVariable Long id, @Valid @RequestBody JobRequestDTO dto) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseDTO.success( service.update(id, dto),"Job updated successfully"));
    }

    @DeleteMapping("/api/v1/delete/{id}")
    public ResponseEntity<ApiResponseDTO<Void>> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseDTO.success( null,"Job deleted successfully"));
    }

    @GetMapping("/api/v1/search")
    public ResponseEntity<ApiResponseDTO<PagedResponseDTO<JobDTO>>> search(@Valid JobSearchDTO searchDTO) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseDTO.success( service.search(searchDTO),"Jobs searched successfully"));
    }

    @GetMapping("/api/v1/{id}/applications")
    public ResponseEntity<ApiResponseDTO<List<ApplicationDTO>>> getJobApplications(@PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseDTO.success( service.getJobApplications(id),"Job applications retrieved successfully"));
    }

    @PutMapping("/api/v1/{id}/status")
    public ResponseEntity<ApiResponseDTO<JobDTO>> updateStatus(@PathVariable Long id, @RequestParam String status) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseDTO.success( service.updateStatus(id, status),"Job status updated successfully"));
    }
}