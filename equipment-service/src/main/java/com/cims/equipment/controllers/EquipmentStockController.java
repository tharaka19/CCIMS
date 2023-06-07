package com.cims.equipment.controllers;

import com.cims.equipment.dtos.EquipmentStockDTO;
import com.cims.equipment.services.EquipmentStockService;
import com.cims.equipment.utils.ResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * The EquipmentStockController class handles HTTP requests for Equipment Stock management endpoints.
 */
@RestController
@RequestMapping("/equipment/equipmentStock")
public class EquipmentStockController {

    @Autowired
    private EquipmentStockService equipmentStockService;

    /**
     * Saves or updates a Equipment Stock in the database.
     *
     * @param token            the authorization token for the request.
     * @param equipmentStockDTO the DTO representing the Equipment Stock to be saved or updated.
     * @return a ResponseEntity containing the ResponseDTO with the result of the operation and an HTTP status code.
     */
    @PostMapping("/saveUpdate")
    public ResponseEntity<ResponseDTO> saveUpdate(@RequestHeader(name = "Authorization") String token, @RequestBody EquipmentStockDTO equipmentStockDTO) {
        return equipmentStockService.saveUpdateEquipmentStock(token, equipmentStockDTO);
    }

    @GetMapping("/updateEquipmentQuantity/{id}/{availableQuantity}")
    public ResponseEntity<ResponseDTO> updateEquipmentQuantity(@PathVariable String id, @PathVariable Integer availableQuantity) {
        return equipmentStockService.updateEquipmentQuantity(id, availableQuantity);
    }

    /**
     * Gets all Equipment Stocks from the database.
     *
     * @param token the authorization token for the request.
     * @return a ResponseEntity containing the ResponseDTO with the list of Equipment Stock DTOs and an HTTP status code.
     */
    @GetMapping("/getAll")
    public ResponseEntity<ResponseDTO> getAll(@RequestHeader(name = "Authorization") String token) {
        return equipmentStockService.getAllEquipmentStocks(token);
    }

    /**
     * Gets all active Equipment Stocks from the database.
     *
     * @param token the authorization token for the request.
     * @return a ResponseEntity containing the ResponseDTO with the list of active Equipment Stock DTOs and an HTTP status code.
     */
    @GetMapping("/getAllActive")
    public ResponseEntity<ResponseDTO> getAllActive(@RequestHeader(name = "Authorization") String token) {
        return equipmentStockService.getAllActiveEquipmentStocks(token);
    }

    /**
     * Gets a Equipment Stock by ID from the database.
     *
     * @param token the authorization token for the request.
     * @param id    the ID of the Equipment Stock to retrieve.
     * @return a ResponseEntity containing the ResponseDTO with the Equipment Stock DTO and an HTTP status code.
     */
    @GetMapping("/getById/{id}")
    public ResponseEntity<ResponseDTO> getById(@RequestHeader(name = "Authorization") String token, @PathVariable String id) {
        return equipmentStockService.getEquipmentStockById(id);
    }

    /**
     * Gets a Equipment Stock by Equipment ID from the database.
     *
     * @param token the authorization token for the request.
     * @param equipmentId    the Equipment ID of the Equipment Stock to retrieve.
     * @return a ResponseEntity containing the ResponseDTO with the Equipment Stock DTO and an HTTP status code.
     */
    @GetMapping("/getByEquipmentId/{equipmentId}")
    public ResponseEntity<ResponseDTO> getByEquipmentId(@RequestHeader(name = "Authorization") String token, @PathVariable String equipmentId) {
        return equipmentStockService.getEquipmentStockByEquipmentId(equipmentId);
    }

    /**
     * Deletes a Equipment Stock by ID from the database.
     *
     * @param token the authorization token for the request.
     * @param id    the ID of the Equipment Stock to delete.
     * @return a ResponseEntity containing the ResponseDTO with the result of the operation and an HTTP status code.
     */
    @DeleteMapping("/deleteById/{id}")
    public ResponseEntity<ResponseDTO> deleteById(@RequestHeader(name = "Authorization") String token, @PathVariable String id) {
        return equipmentStockService.deleteEquipmentStockById(id);
    }
}
