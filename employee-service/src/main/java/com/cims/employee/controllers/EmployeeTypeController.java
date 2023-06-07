package com.cims.employee.controllers;

import com.cims.employee.dtos.EmployeeTypeDTO;
import com.cims.employee.services.EmployeeTypeService;
import com.cims.employee.utils.ResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * The EmployeeTypeController class handles HTTP requests for Employee Type management endpoints.
 */
@RestController
@RequestMapping("/employee/employeeType")
public class EmployeeTypeController {

    @Autowired
    private EmployeeTypeService employeeTypeService;

    /**
     * Saves or updates a Employee Type in the database.
     *
     * @param token           the authorization token for the request.
     * @param employeeTypeDTO the DTO representing the Employee Type to be saved or updated.
     * @return a ResponseEntity containing the ResponseDTO with the result of the operation and an HTTP status code.
     */
    @PostMapping("/saveUpdate")
    public ResponseEntity<ResponseDTO> saveUpdate(@RequestHeader(name = "Authorization") String token, @RequestBody EmployeeTypeDTO employeeTypeDTO) {
        return employeeTypeService.saveUpdateEmployeeType(token, employeeTypeDTO);
    }

    /**
     * Gets all Employee Types from the database.
     *
     * @param token the authorization token for the request.
     * @return a ResponseEntity containing the ResponseDTO with the list of Employee Type DTOs and an HTTP status code.
     */
    @GetMapping("/getAll")
    public ResponseEntity<ResponseDTO> getAll(@RequestHeader(name = "Authorization") String token) {
        return employeeTypeService.getAllEmployeeTypes(token);
    }

    /**
     * Gets all active Employee Types from the database.
     *
     * @param token the authorization token for the request.
     * @return a ResponseEntity containing the ResponseDTO with the list of active Employee Type DTOs and an HTTP status code.
     */
    @GetMapping("/getAllActive")
    public ResponseEntity<ResponseDTO> getAllActive(@RequestHeader(name = "Authorization") String token) {
        return employeeTypeService.getAllActiveEmployeeTypes(token);
    }

    /**
     * Gets a Employee Type by ID from the database.
     *
     * @param token the authorization token for the request.
     * @param id    the ID of the Employee Type to retrieve.
     * @return a ResponseEntity containing the ResponseDTO with the Employee Type DTO and an HTTP status code.
     */
    @GetMapping("/getById/{id}")
    public ResponseEntity<ResponseDTO> getById(@RequestHeader(name = "Authorization") String token, @PathVariable String id) {
        return employeeTypeService.getEmployeeTypeById(id);
    }

    /**
     * Deletes a Employee Type by ID from the database.
     *
     * @param token the authorization token for the request.
     * @param id    the ID of the Employee Type to delete.
     * @return a ResponseEntity containing the ResponseDTO with the result of the operation and an HTTP status code.
     */
    @DeleteMapping("/deleteById/{id}")
    public ResponseEntity<ResponseDTO> deleteById(@RequestHeader(name = "Authorization") String token, @PathVariable String id) {
        return employeeTypeService.deleteEmployeeTypeById(id);
    }
}
