package com.jobportal.dto;

import com.jobportal.entity.Document;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DocumentDTO {
    private Long documentId;
    private UserDTO user;
    private Document.DocumentType documentType;
    private String fileName;
    private String fileUrl;
    private Long fileSize;
    private Boolean isVerified;
    private LocalDateTime uploadedAt;
    
    public static DocumentDTO fromEntity(Document document) {
        DocumentDTO dto = new DocumentDTO();
        dto.setDocumentId(document.getDocumentId());
        dto.setUser(UserDTO.fromEntity(document.getUser()));
        dto.setDocumentType(document.getDocumentType());
        dto.setFileName(document.getFileName());
        dto.setFileUrl(document.getFileUrl());
        dto.setFileSize(document.getFileSize());
        dto.setIsVerified(document.getIsVerified());
        dto.setUploadedAt(document.getUploadedAt());
        return dto;
    }
}