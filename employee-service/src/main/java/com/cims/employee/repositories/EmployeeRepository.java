package com.cims.employee.repositories;

import com.cims.employee.entities.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * This is a repository interface for performing database operations on Employee entities.
 */
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    List<Employee> findAllByBranchCode(String branchCode);

    Employee findByNic(String nic);

    Employee findByEmployeeNumberAndBranchCode(String employeeNumber, String branchCode);
}