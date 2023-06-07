package com.cims.employee.repositories;

import com.cims.employee.entities.EmployeeHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * This is a repository interface for performing database operations on EmployeeHistory entities.
 */
@Repository
public interface EmployeeHistoryRepository extends JpaRepository<EmployeeHistory, Long> {
    List<EmployeeHistory> findAllByBranchCode(String branchCode);
}
