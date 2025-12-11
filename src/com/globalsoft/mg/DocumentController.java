package com.globalsoft.mg;

import com.jobportal.dto.*;
import com.jobportal.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService service;

    @PostMapping("/api/v1/upload")
    public ResponseEntity<ApiResponseDTO<DocumentDTO>> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam Long userId,
            @RequestParam String documentType) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponseDTO.success( service.upload(file, userId, documentType),"Document uploaded successfully"));
    }

    @GetMapping("/api/v1/{id}")
    public ResponseEntity<ApiResponseDTO<DocumentDTO>> getById(@PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseDTO.success( service.getById(id),"Document retrieved successfully"));
    }

    @GetMapping("/api/v1")
    public ResponseEntity<ApiResponseDTO<List<DocumentDTO>>> getAll() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseDTO.success(service.getAll(),"Documents retrieved successfully"));
    }

    @DeleteMapping("/api/v1/delete/{id}")
    public ResponseEntity<ApiResponseDTO<Void>> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseDTO.success( null,"Document deleted successfully"));
    }

    @GetMapping("/api/v1/user/{userId}")
    public ResponseEntity<ApiResponseDTO<List<DocumentDTO>>> getByUserId(@PathVariable Long userId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseDTO.success( service.getByUserId(userId),"User documents retrieved successfully"));
    }

    @PutMapping("/api/v1/{id}/verify")
    public ResponseEntity<ApiResponseDTO<DocumentDTO>> verifyDocument(@PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseDTO.success( service.verifyDocument(id),"Document verified successfully"));
    }
}