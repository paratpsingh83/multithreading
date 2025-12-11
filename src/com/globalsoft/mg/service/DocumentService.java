package com.jobportal.service;

import com.jobportal.dto.DocumentDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DocumentService {
    DocumentDTO upload(MultipartFile file, Long userId, String documentType);

    DocumentDTO getById(Long id);

    List<DocumentDTO> getAll();

    void delete(Long id);

    List<DocumentDTO> getByUserId(Long userId);

    DocumentDTO verifyDocument(Long id);

    List<DocumentDTO> getByUserAndType(Long userId, String documentType);
}