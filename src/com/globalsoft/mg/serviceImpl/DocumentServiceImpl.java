package com.jobportal.serviceImpl;

import com.jobportal.dto.DocumentDTO;
import com.jobportal.entity.Document;
import com.jobportal.entity.User;
import com.jobportal.exception.ExceptionUtils;
import com.jobportal.exception.FileStorageException;
import com.jobportal.repository.DocumentRepository;
import com.jobportal.repository.UserRepository;
import com.jobportal.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;

    private final Path fileStorageLocation = Paths.get("uploads/documents").toAbsolutePath().normalize();
    private final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    @Override
    @Transactional
    public DocumentDTO upload(MultipartFile file, Long userId, String documentType) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> ExceptionUtils.resourceNotFound("User", "id", userId));

            // Validate file
            validateFile(file);

            // Validate document type
            Document.DocumentType type = validateDocumentType(documentType);

            // Create upload directory if it doesn't exist
            createUploadDirectory();

            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String fileExtension = getFileExtension(originalFilename);
            String storedFilename = UUID.randomUUID().toString() + fileExtension;

            // Save file
            Path targetLocation = fileStorageLocation.resolve(storedFilename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // Create document record
            Document document = new Document();
            document.setUser(user);
            document.setDocumentType(type);
            document.setFileName(originalFilename);
            document.setFileUrl(targetLocation.toString());
            document.setFileSize(file.getSize());
            document.setIsVerified(false);

            Document savedDocument = documentRepository.save(document);

            // Notify admin for verification if needed
            if (type == Document.DocumentType.RESUME || type == Document.DocumentType.CERTIFICATE) {
                // In real implementation, notify admin users
            }

            return DocumentDTO.fromEntity(savedDocument);

        } catch (IOException ex) {
            throw new FileStorageException("Could not store file: " + ex.getMessage(), ex);
        }
    }

    @Override
    public DocumentDTO getById(Long id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> ExceptionUtils.resourceNotFound("Document", "id", id));
        return DocumentDTO.fromEntity(document);
    }

    @Override
    public List<DocumentDTO> getAll() {
        return documentRepository.findAll()
                .stream()
                .map(DocumentDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> ExceptionUtils.resourceNotFound("Document", "id", id));

        try {
            // Delete physical file
            Path filePath = Paths.get(document.getFileUrl());
            Files.deleteIfExists(filePath);

            // Delete database record
            documentRepository.delete(document);
        } catch (IOException ex) {
            throw new FileStorageException("Could not delete file: " + ex.getMessage(), ex);
        }
    }

    @Override
    public List<DocumentDTO> getByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw ExceptionUtils.resourceNotFound("User", "id", userId);
        }

        return documentRepository.findByUserUserId(userId)
                .stream()
                .map(DocumentDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public DocumentDTO verifyDocument(Long id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> ExceptionUtils.resourceNotFound("Document", "id", id));

        if (document.getIsVerified()) {
            throw ExceptionUtils.businessError("Document is already verified");
        }

        document.setIsVerified(true);
        Document verifiedDocument = documentRepository.save(document);

        // Notify user about verification
        // notificationService.createNotification(...)

        return DocumentDTO.fromEntity(verifiedDocument);
    }

    @Override
    public List<DocumentDTO> getByUserAndType(Long userId, String documentType) {
        if (!userRepository.existsById(userId)) {
            throw ExceptionUtils.resourceNotFound("User", "id", userId);
        }

        try {
            Document.DocumentType type = Document.DocumentType.valueOf(documentType.toUpperCase());
            return documentRepository.findByUserIdAndDocumentType(userId, type)
                    .stream()
                    .map(DocumentDTO::fromEntity)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException ex) {
            throw ExceptionUtils.validationError("Invalid document type: " + documentType);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw ExceptionUtils.validationError("File cannot be empty");
        }

        // Validate file size
        if (file.getSize() > MAX_FILE_SIZE) {
            throw ExceptionUtils.validationError("File size must be less than 10MB");
        }

        // Validate file type
        String contentType = file.getContentType();
        if (contentType == null || !isSupportedContentType(contentType)) {
            throw ExceptionUtils.validationError(
                    "File type not supported. Supported types: PDF, DOC, DOCX, JPEG, PNG, JPG"
            );
        }

        // Validate filename
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.contains("..")) {
            throw ExceptionUtils.validationError("Invalid file name");
        }
    }

    private Document.DocumentType validateDocumentType(String documentType) {
        try {
            return Document.DocumentType.valueOf(documentType.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw ExceptionUtils.validationError(
                    "Invalid document type. Supported types: " +
                            java.util.Arrays.stream(Document.DocumentType.values())
                                    .map(Enum::name)
                                    .collect(Collectors.joining(", "))
            );
        }
    }

    private void createUploadDirectory() throws IOException {
        if (!Files.exists(fileStorageLocation)) {
            Files.createDirectories(fileStorageLocation);
        }
    }

    private boolean isSupportedContentType(String contentType) {
        return List.of(
                "application/pdf",
                "application/msword",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "image/jpeg",
                "image/jpg",
                "image/png"
        ).contains(contentType);
    }

    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }

    // Additional method to get document file for download
    public byte[] getDocumentFile(Long id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> ExceptionUtils.resourceNotFound("Document", "id", id));

        try {
            Path filePath = Paths.get(document.getFileUrl());
            return Files.readAllBytes(filePath);
        } catch (IOException ex) {
            throw new FileStorageException("Could not read file: " + ex.getMessage(), ex);
        }
    }
}