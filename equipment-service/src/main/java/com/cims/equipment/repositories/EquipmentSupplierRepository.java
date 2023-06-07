package com.cims.equipment.repositories;

import com.cims.equipment.entities.EquipmentSupplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * This is a repository interface for performing database operations on EquipmentSupplier entities.
 */
@Repository
public interface EquipmentSupplierRepository extends JpaRepository<EquipmentSupplier, Long> {
    List<EquipmentSupplier> findAllByBranchCode(String branchCode);

    EquipmentSupplier findBySupplierNumberAndBranchCode(String supplierNumber, String branchCode);
}
