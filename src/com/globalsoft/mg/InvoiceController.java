package com.globalsoft.mg;

import com.jobportal.dto.*;
import com.jobportal.service.InvoiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService service;

    @PostMapping("/api/v1/create")
    public ResponseEntity<ApiResponseDTO<InvoiceDTO>> create(@Valid @RequestBody InvoiceRequestDTO dto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponseDTO.success(service.create(dto),"Invoice created successfully"));
    }

    @GetMapping("/api/v1/{id}")
    public ResponseEntity<ApiResponseDTO<InvoiceDTO>> getById(@PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseDTO.success( service.getById(id),"Invoice retrieved successfully"));
    }

    @GetMapping("/api/v1")
    public ResponseEntity<ApiResponseDTO<List<InvoiceDTO>>> getAll() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseDTO.success( service.getAll(),"Invoices retrieved successfully"));
    }

    @PutMapping("/api/v1/update/{id}")
    public ResponseEntity<ApiResponseDTO<InvoiceDTO>> update(@PathVariable Long id, @Valid @RequestBody InvoiceRequestDTO dto) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseDTO.success( service.update(id, dto),"Invoice updated successfully"));
    }

    @DeleteMapping("/api/v1/delete/{id}")
    public ResponseEntity<ApiResponseDTO<Void>> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseDTO.success( null,"Invoice deleted successfully"));
    }

    @PutMapping("/api/v1/{id}/status")
    public ResponseEntity<ApiResponseDTO<InvoiceDTO>> updateStatus(@PathVariable Long id, @Valid @RequestBody InvoiceStatusDTO dto) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseDTO.success( service.updateStatus(id, dto),"Invoice status updated successfully"));
    }

    @GetMapping("/api/v1/placement/{placementId}")
    public ResponseEntity<ApiResponseDTO<List<InvoiceDTO>>> getByPlacementId(@PathVariable Long placementId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseDTO.success( service.getByPlacementId(placementId),"Placement invoices retrieved successfully"));
    }

    @GetMapping("/api/v1/status/{status}")
    public ResponseEntity<ApiResponseDTO<List<InvoiceDTO>>> getByStatus(@PathVariable String status) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseDTO.success( service.getByStatus(status),"Invoices by status retrieved successfully"));
    }

    @PostMapping("/api/v1/generate")
    public ResponseEntity<ApiResponseDTO<InvoiceDTO>> generateInvoice(@RequestParam Long placementId, @RequestParam String period) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponseDTO.success( service.generateInvoice(placementId, period),"Invoice generated successfully"));
    }
}