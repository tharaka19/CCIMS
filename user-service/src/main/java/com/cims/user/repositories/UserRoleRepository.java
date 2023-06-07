package com.cims.user.repositories;

import com.cims.user.entities.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * This is a repository interface for performing database operations on UserRole entities.
 */
@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    List<UserRole> findAllByBranchCode(String branchCode);

    UserRole findByRoleNameAndBranchCode(String roleName, String branchCode);
}
