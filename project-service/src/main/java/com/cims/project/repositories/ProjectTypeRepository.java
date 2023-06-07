package com.cims.project.repositories;

import com.cims.project.entities.ProjectType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * This is a repository interface for performing database operations on ProjectType entities.
 */
@Repository
public interface ProjectTypeRepository extends JpaRepository<ProjectType, Long> {
    List<ProjectType> findAllByBranchCode(String branchCode);

    ProjectType findByProjectTypeAndBranchCode(String equipmentType, String branchCode);
}
