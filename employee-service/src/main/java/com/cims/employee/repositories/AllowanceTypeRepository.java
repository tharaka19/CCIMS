package com.cims.employee.repositories;

import com.cims.employee.entities.AllowanceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * This is a repository interface for performing database operations on AllowanceType entities.
 */
@Repository
public interface AllowanceTypeRepository extends JpaRepository<AllowanceType, Long> {
    List<AllowanceType> findAllByBranchCode(String branchCode);

    AllowanceType findByAllowanceTypeAndBranchCode(String allowanceType, String branchCode);
}
