package com.cims.equipment.controllers;

import com.cims.equipment.dtos.EquipmentStockHistoryDTO;
import com.cims.equipment.services.EquipmentStockHistoryService;
import com.cims.equipment.utils.ResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * The EquipmentStockHistoryController class handles HTTP requests for Equipment Stock History management endpoints.
 */
@RestController
@RequestMapping("/equipment/equipmentStockHistory")
public class EquipmentStockHistoryController {

    @Autowired
    private EquipmentStockHistoryService equipmentStockHistoryService;

    /**
     * Saves or updates a Equipment Stock History in the database.
     *
     * @param token            the authorization token for the request.
     * @param equipmentStockDTO the DTO representing the Equipment Stock History to be saved or updated.
     * @return a ResponseEntity containing the ResponseDTO with the result of the operation and an HTTP status code.
     */
    @PostMapping("/saveUpdate")
    public ResponseEntity<ResponseDTO> saveUpdate(@RequestHeader(name = "Authorization") String token, @RequestBody EquipmentStockHistoryDTO equipmentStockDTO) {
        return equipmentStockHistoryService.saveUpdateEquipmentStockHistory(token, equipmentStockDTO);
    }

    /**
     * Gets all Equipment Stock History from the database by Equipment Stock.
     *
     * @param token the authorization token for the request.
     * @return a ResponseEntity containing the ResponseDTO with the list of Equipment Stock History DTOs and an HTTP status code.
     */
    @GetMapping("/getAllByEquipmentStock/{equipmentStockId}")
    public ResponseEntity<ResponseDTO> getAllByEquipmentStock(@RequestHeader(name = "Authorization") String token, @PathVariable String equipmentStockId) {
        return equipmentStockHistoryService.getAllEquipmentStockHistoryByEquipmentHistory(token, equipmentStockId);
    }
}
