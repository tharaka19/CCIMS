package com.cims.equipment.controllers;

import com.cims.equipment.dtos.EquipmentDTO;
import com.cims.equipment.services.EquipmentService;
import com.cims.equipment.utils.ResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * The EquipmentController class handles HTTP requests for Equipment management endpoints.
 */
@RestController
@RequestMapping("/equipment/equipment")
public class EquipmentController {

    @Autowired
    private EquipmentService equipmentService;

    /**
     * Saves or updates a Equipment in the database.
     *
     * @param token        the authorization token for the request.
     * @param equipmentDTO the DTO representing the Equipment to be saved or updated.
     * @return a ResponseEntity containing the ResponseDTO with the result of the operation and an HTTP status code.
     */
    @PostMapping("/saveUpdate")
    public ResponseEntity<ResponseDTO> saveUpdate(@RequestHeader(name = "Authorization") String token, @RequestBody EquipmentDTO equipmentDTO) {
        return equipmentService.saveUpdateEquipment(token, equipmentDTO);
    }

    /**
     * Gets all Equipments from the database.
     *
     * @param token the authorization token for the request.
     * @return a ResponseEntity containing the ResponseDTO with the list of Equipment DTOs and an HTTP status code.
     */
    @GetMapping("/getAll")
    public ResponseEntity<ResponseDTO> getAll(@RequestHeader(name = "Authorization") String token) {
        return equipmentService.getAllEquipments(token);
    }

    /**
     * Gets all active Equipments from the database.
     *
     * @param token the authorization token for the request.
     * @return a ResponseEntity containing the ResponseDTO with the list of active Equipment DTOs and an HTTP status code.
     */
    @GetMapping("/getAllActive")
    public ResponseEntity<ResponseDTO> getAllActive(@RequestHeader(name = "Authorization") String token) {
        return equipmentService.getAllActiveEquipments(token);
    }

    /**
     * Gets all active Equipments by Equipment Type from the database.
     *
     * @param token the authorization token for the request.
     * @return a ResponseEntity containing the ResponseDTO with the list of active Equipment DTOs and an HTTP status code.
     */
    @GetMapping("/getAllActiveByEquipmentType/{equipmentTypeId}")
    public ResponseEntity<ResponseDTO> getAllActiveByEquipmentType(@RequestHeader(name = "Authorization") String token, @PathVariable String equipmentTypeId) {
        return equipmentService.getAllActiveEquipmentsByEquipmentType(token, equipmentTypeId);
    }

    /**
     * Gets a Equipment by ID from the database.
     *
     * @param token the authorization token for the request.
     * @param id    the ID of the Equipment to retrieve.
     * @return a ResponseEntity containing the ResponseDTO with the Equipment DTO and an HTTP status code.
     */
    @GetMapping("/getById/{id}")
    public ResponseEntity<ResponseDTO> getById(@RequestHeader(name = "Authorization") String token, @PathVariable String id) {
        return equipmentService.getEquipmentById(id);
    }

    /**
     * Deletes a Equipment by ID from the database.
     *
     * @param token the authorization token for the request.
     * @param id    the ID of the Equipment to delete.
     * @return a ResponseEntity containing the ResponseDTO with the result of the operation and an HTTP status code.
     */
    @DeleteMapping("/deleteById/{id}")
    public ResponseEntity<ResponseDTO> deleteById(@RequestHeader(name = "Authorization") String token, @PathVariable String id) {
        return equipmentService.deleteEquipmentById(id);
    }
}
