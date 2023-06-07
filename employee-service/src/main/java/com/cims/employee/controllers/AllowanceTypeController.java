package com.cims.employee.controllers;

import com.cims.employee.dtos.AllowanceTypeDTO;
import com.cims.employee.services.AllowanceTypeService;
import com.cims.employee.utils.ResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * The AllowanceTypeController class handles HTTP requests for Allowance Type management endpoints.
 */
@RestController
@RequestMapping("/employee/allowanceType")
public class AllowanceTypeController {

    @Autowired
    private AllowanceTypeService allowanceTypeService;

    /**
     * Saves or updates a Allowance Type in the database.
     *
     * @param token            the authorization token for the request.
     * @param allowanceTypeDTO the DTO representing the Allowance Type to be saved or updated.
     * @return a ResponseEntity containing the ResponseDTO with the result of the operation and an HTTP status code.
     */
    @PostMapping("/saveUpdate")
    public ResponseEntity<ResponseDTO> saveUpdate(@RequestHeader(name = "Authorization") String token, @RequestBody AllowanceTypeDTO allowanceTypeDTO) {
        return allowanceTypeService.saveUpdateAllowanceType(token, allowanceTypeDTO);
    }

    /**
     * Gets all Allowance Types from the database.
     *
     * @param token the authorization token for the request.
     * @return a ResponseEntity containing the ResponseDTO with the list of Allowance Type DTOs and an HTTP status code.
     */
    @GetMapping("/getAll")
    public ResponseEntity<ResponseDTO> getAll(@RequestHeader(name = "Authorization") String token) {
        return allowanceTypeService.getAllAllowanceTypes(token);
    }

    /**
     * Gets all active Allowance Types from the database.
     *
     * @param token the authorization token for the request.
     * @return a ResponseEntity containing the ResponseDTO with the list of active Allowance Type DTOs and an HTTP status code.
     */
    @GetMapping("/getAllActive")
    public ResponseEntity<ResponseDTO> getAllActive(@RequestHeader(name = "Authorization") String token) {
        return allowanceTypeService.getAllActiveAllowanceTypes(token);
    }

    /**
     * Gets a Allowance Type by ID from the database.
     *
     * @param token the authorization token for the request.
     * @param id    the ID of the Allowance Type to retrieve.
     * @return a ResponseEntity containing the ResponseDTO with the Allowance Type DTO and an HTTP status code.
     */
    @GetMapping("/getById/{id}")
    public ResponseEntity<ResponseDTO> getById(@RequestHeader(name = "Authorization") String token, @PathVariable String id) {
        return allowanceTypeService.getAllowanceTypeById(id);
    }

    /**
     * Deletes a Allowance Type by ID from the database.
     *
     * @param token the authorization token for the request.
     * @param id    the ID of the Allowance Type to delete.
     * @return a ResponseEntity containing the ResponseDTO with the result of the operation and an HTTP status code.
     */
    @DeleteMapping("/deleteById/{id}")
    public ResponseEntity<ResponseDTO> deleteById(@RequestHeader(name = "Authorization") String token, @PathVariable String id) {
        return allowanceTypeService.deleteAllowanceTypeById(id);
    }
}
