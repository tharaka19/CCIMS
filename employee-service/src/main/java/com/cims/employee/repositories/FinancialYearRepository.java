package com.cims.employee.repositories;

import com.cims.employee.entities.FinancialYear;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * This is a repository interface for performing database operations on FinancialYear entities.
 */
@Repository
public interface FinancialYearRepository extends JpaRepository<FinancialYear, Long> {
    List<FinancialYear> findAllByBranchCode(String branchCode);

    FinancialYear findByFinancialYearAndBranchCode(String financialYear, String branchCode);
}
