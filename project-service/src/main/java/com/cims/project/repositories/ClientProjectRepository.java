package com.cims.project.repositories;

import com.cims.project.entities.ClientProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * This is a repository interface for performing database operations on client Project entities.
 */
@Repository
public interface ClientProjectRepository extends JpaRepository<ClientProject, Long> {
    List<ClientProject> findAllByBranchCode(String branchCode);

    ClientProject findByProjectNumberAndBranchCode(String projectNumber, String branchCode);
}
