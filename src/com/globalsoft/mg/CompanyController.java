package com.globalsoft.mg;

import com.jobportal.dto.*;
import com.jobportal.service.CompanyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService service;

    @PostMapping("/api/v1/create")
    public ResponseEntity<ApiResponseDTO<CompanyDTO>> create(@Valid @RequestBody CompanyRequestDTO dto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponseDTO.success( service.create(dto),"Company created successfully"));
    }

    @GetMapping("/api/v1/{id}")
    public ResponseEntity<ApiResponseDTO<CompanyDTO>> getById(@PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseDTO.success( service.getById(id),"Company retrieved successfully"));
    }

    @GetMapping("/api/v1")
    public ResponseEntity<ApiResponseDTO<List<CompanyDTO>>> getAll() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseDTO.success( service.getAll(),"Companies retrieved successfully"));
    }

    @PutMapping("/api/v1/update/{id}")
    public ResponseEntity<ApiResponseDTO<CompanyDTO>> update(@PathVariable Long id, @Valid @RequestBody CompanyRequestDTO dto) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseDTO.success( service.update(id, dto),"Company updated successfully"));
    }

    @DeleteMapping("/api/v1/delete/{id}")
    public ResponseEntity<ApiResponseDTO<String>> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponseDTO.success("Company deleted successfully", null));
    }

    @GetMapping("/api/v1/{id}/jobs")
    public ResponseEntity<ApiResponseDTO<List<JobDTO>>> getCompanyJobs(@PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseDTO.success( service.getCompanyJobs(id),"Company jobs retrieved successfully"));
    }
}