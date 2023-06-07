package com.cims.equipment.repositories;

import com.cims.equipment.entities.EquipmentStockHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * This is a repository interface for performing database operations on EquipmentStockHistory entities.
 */
@Repository
public interface EquipmentStockHistoryRepository extends JpaRepository<EquipmentStockHistory, Long> {
    List<EquipmentStockHistory> findAllByBranchCode(String branchCode);
}
