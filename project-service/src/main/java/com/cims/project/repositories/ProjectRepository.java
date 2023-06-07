package com.cims.project.repositories;

import com.cims.project.entities.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * This is a repository interface for performing database operations on Project entities.
 */
@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findAllByBranchCode(String branchCode);

    Project findByProjectNumberAndBranchCode(String projectNumber, String branchCode);
}
