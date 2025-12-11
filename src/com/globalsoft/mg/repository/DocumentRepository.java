package com.jobportal.repository;

import com.jobportal.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByUserUserId(Long userId);

    List<Document> findByDocumentType(Document.DocumentType documentType);

    List<Document> findByIsVerified(Boolean isVerified);

    @Query("SELECT d FROM Document d WHERE d.user.userId = :userId AND d.documentType = :documentType")
    List<Document> findByUserIdAndDocumentType(@Param("userId") Long userId,
                                               @Param("documentType") Document.DocumentType documentType);

    @Query("SELECT COUNT(d) FROM Document d WHERE d.user.userId = :userId AND d.isVerified = true")
    Long countVerifiedDocumentsByUserId(@Param("userId") Long userId);
}