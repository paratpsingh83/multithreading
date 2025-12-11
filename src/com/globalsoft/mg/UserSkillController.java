package com.globalsoft.mg;

import com.jobportal.dto.*;
import com.jobportal.service.UserSkillService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user-skills")
@RequiredArgsConstructor
public class UserSkillController {

    private final UserSkillService service;

    @PostMapping("/api/v1/create")
    public ResponseEntity<ApiResponseDTO<UserSkillDTO>> create(@Valid @RequestBody UserSkillRequestDTO dto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponseDTO.success( service.create(dto),"User skill created successfully"));
    }

    @GetMapping("/api/v1/{id}")
    public ResponseEntity<ApiResponseDTO<UserSkillDTO>> getById(@PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseDTO.success( service.getById(id),"User skill retrieved successfully"));
    }

    @GetMapping("/api/v1/user/{userId}")
    public ResponseEntity<ApiResponseDTO<List<UserSkillDTO>>> getByUserId(@PathVariable Long userId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseDTO.success( service.getByUserId(userId),"User skills retrieved successfully"));
    }

    @PutMapping("/api/v1/update/{id}")
    public ResponseEntity<ApiResponseDTO<UserSkillDTO>> update(@PathVariable Long id, @Valid @RequestBody UserSkillRequestDTO dto) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseDTO.success( service.update(id, dto),"User skill updated successfully"));
    }

    @DeleteMapping("/api/v1/delete/{id}")
    public ResponseEntity<ApiResponseDTO<Void>> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseDTO.success( null,"User skill deleted successfully"));
    }
}