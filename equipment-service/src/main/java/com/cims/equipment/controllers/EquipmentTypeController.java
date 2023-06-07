package com.cims.equipment.controllers;

import com.cims.equipment.dtos.EquipmentTypeDTO;
import com.cims.equipment.services.EquipmentTypeService;
import com.cims.equipment.utils.ResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * The EquipmentTypeController class handles HTTP requests for Equipment Type management endpoints.
 */
@RestController
@RequestMapping("/equipment/equipmentType")
public class EquipmentTypeController {

    @Autowired
    private EquipmentTypeService equipmentTypeService;

    /**
     * Saves or updates a Equipment Type in the database.
     *
     * @param token            the authorization token for the request.
     * @param equipmentTypeDTO the DTO representing the Equipment Type to be saved or updated.
     * @return a ResponseEntity containing the ResponseDTO with the result of the operation and an HTTP status code.
     */
    @PostMapping("/saveUpdate")
    public ResponseEntity<ResponseDTO> saveUpdate(@RequestHeader(name = "Authorization") String token, @RequestBody EquipmentTypeDTO equipmentTypeDTO) {
        return equipmentTypeService.saveUpdateEquipmentType(token, equipmentTypeDTO);
    }

    /**
     * Gets all Equipment Types from the database.
     *
     * @param token the authorization token for the request.
     * @return a ResponseEntity containing the ResponseDTO with the list of Equipment Type DTOs and an HTTP status code.
     */
    @GetMapping("/getAll")
    public ResponseEntity<ResponseDTO> getAll(@RequestHeader(name = "Authorization") String token) {
        return equipmentTypeService.getAllEquipmentTypes(token);
    }

    /**
     * Gets all active Equipment Types from the database.
     *
     * @param token the authorization token for the request.
     * @return a ResponseEntity containing the ResponseDTO with the list of active Equipment Type DTOs and an HTTP status code.
     */
    @GetMapping("/getAllActive")
    public ResponseEntity<ResponseDTO> getAllActive(@RequestHeader(name = "Authorization") String token) {
        return equipmentTypeService.getAllActiveEquipmentTypes(token);
    }

    /**
     * Gets a Equipment Type by ID from the database.
     *
     * @param token the authorization token for the request.
     * @param id    the ID of the Equipment Type to retrieve.
     * @return a ResponseEntity containing the ResponseDTO with the Equipment Type DTO and an HTTP status code.
     */
    @GetMapping("/getById/{id}")
    public ResponseEntity<ResponseDTO> getById(@RequestHeader(name = "Authorization") String token, @PathVariable String id) {
        return equipmentTypeService.getEquipmentTypeById(id);
    }

    /**
     * Deletes a Equipment Type by ID from the database.
     *
     * @param token the authorization token for the request.
     * @param id    the ID of the Equipment Type to delete.
     * @return a ResponseEntity containing the ResponseDTO with the result of the operation and an HTTP status code.
     */
    @DeleteMapping("/deleteById/{id}")
    public ResponseEntity<ResponseDTO> deleteById(@RequestHeader(name = "Authorization") String token, @PathVariable String id) {
        return equipmentTypeService.deleteEquipmentTypeById(id);
    }
}
