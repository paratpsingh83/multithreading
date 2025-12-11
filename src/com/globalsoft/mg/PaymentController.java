package com.globalsoft.mg;

import com.jobportal.dto.*;
import com.jobportal.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService service;

    @PostMapping("/api/v1/create")
    public ResponseEntity<ApiResponseDTO<PaymentDTO>> create(@Valid @RequestBody PaymentRequestDTO dto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponseDTO.success( service.create(dto),"Payment created successfully"));
    }

    @GetMapping("/api/v1/{id}")
    public ResponseEntity<ApiResponseDTO<PaymentDTO>> getById(@PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseDTO.success( service.getById(id),"Payment retrieved successfully"));
    }

    @GetMapping("/api/v1")
    public ResponseEntity<ApiResponseDTO<List<PaymentDTO>>> getAll() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseDTO.success( service.getAll(),"Payments retrieved successfully"));
    }

    @PutMapping("/api/v1/update/{id}")
    public ResponseEntity<ApiResponseDTO<PaymentDTO>> update(@PathVariable Long id, @Valid @RequestBody PaymentRequestDTO dto) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseDTO.success( service.update(id, dto),"Payment updated successfully"));
    }

    @DeleteMapping("/api/v1/delete/{id}")
    public ResponseEntity<ApiResponseDTO<Void>> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseDTO.success( null,"Payment deleted successfully"));
    }

    @GetMapping("/api/v1/invoice/{invoiceId}")
    public ResponseEntity<ApiResponseDTO<List<PaymentDTO>>> getByInvoiceId(@PathVariable Long invoiceId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseDTO.success( service.getByInvoiceId(invoiceId),"Invoice payments retrieved successfully"));
    }

    @GetMapping("/api/v1/stats")
    public ResponseEntity<ApiResponseDTO<RevenueMetricsDTO>> getPaymentStats() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseDTO.success( service.getPaymentStats(),"Payment stats retrieved successfully"));
    }
}