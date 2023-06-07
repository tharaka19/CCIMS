package com.cims.employee.repositories;

import com.cims.employee.entities.Employee;
import com.cims.employee.entities.EmployeeProfile;
import com.cims.employee.entities.FinancialYear;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * This is a repository interface for performing database operations on EmployeeProfile entities.
 */
@Repository
public interface EmployeeProfileRepository extends JpaRepository<EmployeeProfile, Long> {
    List<EmployeeProfile> findAllByBranchCode(String branchCode);

    EmployeeProfile findByFinancialYearAndBranchCode(String financialYearId, String branchCode);

    EmployeeProfile findByFinancialYearAndEmployee(FinancialYear financialYear, Employee employee);
}
