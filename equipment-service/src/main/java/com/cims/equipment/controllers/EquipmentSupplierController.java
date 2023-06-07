package com.cims.equipment.controllers;

import com.cims.equipment.dtos.EquipmentSupplierDTO;
import com.cims.equipment.services.EquipmentSupplierService;
import com.cims.equipment.utils.ResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * The EquipmentSupplierController class handles HTTP requests for Equipment Supplier management endpoints.
 */
@RestController
@RequestMapping("/equipment/equipmentSupplier")
public class EquipmentSupplierController {

    @Autowired
    private EquipmentSupplierService equipmentSupplierService;

    /**
     * Saves or updates a Equipment Supplier in the database.
     *
     * @param token        the authorization token for the request.
     * @param equipmentSupplierDTO the DTO representing the Equipment Supplier to be saved or updated.
     * @return a ResponseEntity containing the ResponseDTO with the result of the operation and an HTTP status code.
     */
    @PostMapping("/saveUpdate")
    public ResponseEntity<ResponseDTO> saveUpdate(@RequestHeader(name = "Authorization") String token, @RequestBody EquipmentSupplierDTO equipmentSupplierDTO) {
        return equipmentSupplierService.saveUpdateEquipmentSupplier(token, equipmentSupplierDTO);
    }

    /**
     * Gets all Equipment Suppliers from the database.
     *
     * @param token the authorization token for the request.
     * @return a ResponseEntity containing the ResponseDTO with the list of Equipment Supplier DTOs and an HTTP status code.
     */
    @GetMapping("/getAll")
    public ResponseEntity<ResponseDTO> getAll(@RequestHeader(name = "Authorization") String token) {
        return equipmentSupplierService.getAllEquipmentSuppliers(token);
    }

    /**
     * Gets all active Equipment Suppliers from the database.
     *
     * @param token the authorization token for the request.
     * @return a ResponseEntity containing the ResponseDTO with the list of active Equipment Supplier DTOs and an HTTP status code.
     */
    @GetMapping("/getAllActive")
    public ResponseEntity<ResponseDTO> getAllActive(@RequestHeader(name = "Authorization") String token) {
        return equipmentSupplierService.getAllActiveEquipmentSuppliers(token);
    }

    /**
     * Gets a Equipment Supplier by ID from the database.
     *
     * @param token the authorization token for the request.
     * @param id    the ID of the Equipment Supplier to retrieve.
     * @return a ResponseEntity containing the ResponseDTO with the Equipment Supplier DTO and an HTTP status code.
     */
    @GetMapping("/getById/{id}")
    public ResponseEntity<ResponseDTO> getById(@RequestHeader(name = "Authorization") String token, @PathVariable String id) {
        return equipmentSupplierService.getEquipmentSupplierById(id);
    }

    /**
     * Deletes a Equipment Supplier by ID from the database.
     *
     * @param token the authorization token for the request.
     * @param id    the ID of the Equipment Supplier to delete.
     * @return a ResponseEntity containing the ResponseDTO with the result of the operation and an HTTP status code.
     */
    @DeleteMapping("/deleteById/{id}")
    public ResponseEntity<ResponseDTO> deleteById(@RequestHeader(name = "Authorization") String token, @PathVariable String id) {
        return equipmentSupplierService.deleteEquipmentSupplierById(id);
    }
}
