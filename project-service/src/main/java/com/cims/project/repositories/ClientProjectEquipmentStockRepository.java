package com.cims.project.repositories;

import com.cims.project.entities.ClientProjectEquipmentStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * This is a repository interface for performing database operations on ClientProjectEquipmentStock entities.
 */
@Repository
public interface ClientProjectEquipmentStockRepository extends JpaRepository<ClientProjectEquipmentStock, Long> {
    List<ClientProjectEquipmentStock> findAllByBranchCode(String branchCode);
}
