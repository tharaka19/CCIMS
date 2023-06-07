package com.cims.employee.repositories;

import com.cims.employee.constants.enums.Month;
import com.cims.employee.entities.Employee;
import com.cims.employee.entities.EmployeeSalary;
import com.cims.employee.entities.FinancialYear;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * This is a repository interface for performing database operations on EmployeeSalary entities.
 */
@Repository
public interface EmployeeSalaryRepository extends JpaRepository<EmployeeSalary, Long> {
    List<EmployeeSalary> findAllByBranchCode(String branchCode);

    EmployeeSalary findByFinancialYearAndEmployeeAndSalaryMonth(FinancialYear financialYear, Employee employee, Month salaryMonth);
}
