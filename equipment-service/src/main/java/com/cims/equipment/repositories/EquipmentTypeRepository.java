package com.cims.equipment.repositories;

import com.cims.equipment.entities.EquipmentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * This is a repository interface for performing database operations on EquipmentType entities.
 */
@Repository
public interface EquipmentTypeRepository extends JpaRepository<EquipmentType, Long> {
    List<EquipmentType> findAllByBranchCode(String branchCode);

    EquipmentType findByEquipmentTypeAndBranchCode(String equipmentType, String branchCode);
}
