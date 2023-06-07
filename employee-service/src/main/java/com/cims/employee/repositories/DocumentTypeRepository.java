package com.cims.employee.repositories;

import com.cims.employee.entities.DocumentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * This is a repository interface for performing database operations on DocumentType entities.
 */
@Repository
public interface DocumentTypeRepository extends JpaRepository<DocumentType, Long> {
    List<DocumentType> findAllByBranchCode(String branchCode);

    DocumentType findByDocumentTypeAndBranchCode(String documentType, String branchCode);
}
