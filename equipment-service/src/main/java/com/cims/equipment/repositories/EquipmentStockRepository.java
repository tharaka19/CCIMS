package com.cims.equipment.repositories;

import com.cims.equipment.entities.Equipment;
import com.cims.equipment.entities.EquipmentStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * This is a repository interface for performing database operations on EquipmentStock entities.
 */
@Repository
public interface EquipmentStockRepository extends JpaRepository<EquipmentStock, Long> {
    boolean existsByEquipment(Equipment equipment);

    List<EquipmentStock> findAllByBranchCode(String branchCode);

    EquipmentStock findByEquipment(Equipment equipment);

    EquipmentStock findByStockNumberAndBranchCode(String stockNumber, String branchCode);
}
