package com.cims.equipment.repositories;

import com.cims.equipment.entities.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * This is a repository interface for performing database operations on Equipment entities.
 */
@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Long> {
    List<Equipment> findAllByBranchCode(String branchCode);

    Equipment findByEquipmentNumberAndBranchCode(String equipmentNumber, String branchCode);
}
