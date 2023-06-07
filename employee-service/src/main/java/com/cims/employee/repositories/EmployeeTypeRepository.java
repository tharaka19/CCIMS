package com.cims.employee.repositories;

import com.cims.employee.entities.EmployeeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * This is a repository interface for performing database operations on EmployeeType entities.
 */
@Repository
public interface EmployeeTypeRepository extends JpaRepository<EmployeeType, Long> {
    List<EmployeeType> findAllByBranchCode(String branchCode);

    EmployeeType findByEmployeeTypeAndBranchCode(String employeeType, String branchCode);
}
