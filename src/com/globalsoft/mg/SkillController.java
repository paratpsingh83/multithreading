package com.globalsoft.mg;

import com.jobportal.dto.*;
import com.jobportal.service.SkillService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/skills")
@RequiredArgsConstructor
public class SkillController {

    private final SkillService service;

    @PostMapping("/api/v1/create")
    public ResponseEntity<ApiResponseDTO<SkillDTO>> create(@Valid @RequestBody SkillRequestDTO dto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponseDTO.success( service.create(dto),"Skill created successfully"));
    }

    @GetMapping("/api/v1/{id}")
    public ResponseEntity<ApiResponseDTO<SkillDTO>> getById(@PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseDTO.success( service.getById(id),"Skill retrieved successfully"));
    }

    @GetMapping("/api/v1")
    public ResponseEntity<ApiResponseDTO<List<SkillDTO>>> getAll() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseDTO.success( service.getAll(),"Skills retrieved successfully"));
    }

    @PutMapping("/api/v1/update/{id}")
    public ResponseEntity<ApiResponseDTO<SkillDTO>> update(@PathVariable Long id, @Valid @RequestBody SkillRequestDTO dto) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseDTO.success( service.update(id, dto),"Skill updated successfully"));
    }

    @DeleteMapping("/api/v1/delete/{id}")
    public ResponseEntity<ApiResponseDTO<Void>> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseDTO.success( null,"Skill deleted successfully"));
    }

    @GetMapping("/api/v1/categories")
    public ResponseEntity<ApiResponseDTO<List<String>>> getCategories() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseDTO.success( service.getCategories(),"Skill categories retrieved successfully"));
    }
}