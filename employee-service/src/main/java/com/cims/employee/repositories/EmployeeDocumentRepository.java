package com.cims.employee.repositories;

import com.cims.employee.entities.EmployeeDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * This is a repository interface for performing database operations on EmployeeDocument entities.
 */
@Repository
public interface EmployeeDocumentRepository extends JpaRepository<EmployeeDocument, Long> {
    List<EmployeeDocument> findAllByBranchCode(String branchCode);
}
