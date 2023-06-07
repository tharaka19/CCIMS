package com.cims.employee.controllers;

import com.cims.employee.dtos.EmployeeHistoryDTO;
import com.cims.employee.services.EmployeeHistoryService;
import com.cims.employee.utils.ResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * The EmployeeHistoryController class handles HTTP requests for Employee history management endpoints.
 */
@RestController
@RequestMapping("/employee/employeeHistory")
public class EmployeeHistoryController {

    @Autowired
    private EmployeeHistoryService employeeHistoryService;

    /**
     * Saves or updates a Employee History in the database.
     *
     * @param token           the authorization token for the request.
     * @param employeeHistoryDTO the DTO representing the Employee History to be saved or updated.
     * @return a ResponseEntity containing the ResponseDTO with the result of the operation and an HTTP status code.
     */
    @PostMapping("/saveUpdate")
    public ResponseEntity<ResponseDTO> saveUpdate(@RequestHeader(name = "Authorization") String token, @RequestBody EmployeeHistoryDTO employeeHistoryDTO) {
        return employeeHistoryService.saveUpdateEmployeeHistory(token, employeeHistoryDTO);
    }

    /**
     * Gets all Employee Histories from the database.
     *
     * @param token the authorization token for the request.
     * @return a ResponseEntity containing the ResponseDTO with the list of Employee History DTOs and an HTTP status code.
     */
    @GetMapping("/getAll")
    public ResponseEntity<ResponseDTO> getAll(@RequestHeader(name = "Authorization") String token) {
        return employeeHistoryService.getAllEmployeeHistories(token);
    }

    /**
     * Gets all active Employee Histories from the database.
     *
     * @param token the authorization token for the request.
     * @return a ResponseEntity containing the ResponseDTO with the list of active Employee History DTOs and an HTTP status code.
     */
    @GetMapping("/getAllActive")
    public ResponseEntity<ResponseDTO> getAllActive(@RequestHeader(name = "Authorization") String token) {
        return employeeHistoryService.getAllActiveEmployeeHistories(token);
    }

    /**
     * Gets a Employee History by ID from the database.
     *
     * @param token the authorization token for the request.
     * @param id    the ID of the Employee History to retrieve.
     * @return a ResponseEntity containing the ResponseDTO with the Employee History DTO and an HTTP status code.
     */
    @GetMapping("/getById/{id}")
    public ResponseEntity<ResponseDTO> getById(@RequestHeader(name = "Authorization") String token, @PathVariable String id) {
        return employeeHistoryService.getEmployeeHistoryById(id);
    }

    /**
     * Deletes a Employee History by ID from the database.
     *
     * @param token the authorization token for the request.
     * @param id    the ID of the Employee History to delete.
     * @return a ResponseEntity containing the ResponseDTO with the result of the operation and an HTTP status code.
     */
    @DeleteMapping("/deleteById/{id}")
    public ResponseEntity<ResponseDTO> deleteById(@RequestHeader(name = "Authorization") String token, @PathVariable String id) {
        return employeeHistoryService.deleteEmployeeHistoryById(id);
    }
}
