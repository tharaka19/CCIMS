package com.cims.employee.controllers;

import com.cims.employee.dtos.EmployeeDTO;
import com.cims.employee.services.EmployeeService;
import com.cims.employee.utils.ResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * The EmployeeController class handles HTTP requests for Employee management endpoints.
 */
@RestController
@RequestMapping("/employee/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * Saves or updates a Employee in the database.
     *
     * @param token       the authorization token for the request.
     * @param employeeDTO the DTO representing the Employee to be saved or updated.
     * @return a ResponseEntity containing the ResponseDTO with the result of the operation and an HTTP status code.
     */
    @PostMapping("/saveUpdate")
    public ResponseEntity<ResponseDTO> saveUpdate(@RequestHeader(name = "Authorization") String token, @RequestBody EmployeeDTO employeeDTO) {
        return employeeService.saveUpdateEmployee(token, employeeDTO);
    }

    /**
     * Gets all Employees from the database.
     *
     * @param token the authorization token for the request.
     * @return a ResponseEntity containing the ResponseDTO with the list of Employee DTOs and an HTTP status code.
     */
    @GetMapping("/getAll")
    public ResponseEntity<ResponseDTO> getAll(@RequestHeader(name = "Authorization") String token) {
        return employeeService.getAllEmployees(token);
    }

    /**
     * Gets all active Employees from the database.
     *
     * @param token the authorization token for the request.
     * @return a ResponseEntity containing the ResponseDTO with the list of active Employee DTOs and an HTTP status code.
     */
    @GetMapping("/getAllActive")
    public ResponseEntity<ResponseDTO> getAllActive(@RequestHeader(name = "Authorization") String token) {
        return employeeService.getAllActiveEmployees(token);
    }

    /**
     * Gets a Employee by ID from the database.
     *
     * @param token the authorization token for the request.
     * @param id    the ID of the Employee to retrieve.
     * @return a ResponseEntity containing the ResponseDTO with the Employee DTO and an HTTP status code.
     */
    @GetMapping("/getById/{id}")
    public ResponseEntity<ResponseDTO> getById(@RequestHeader(name = "Authorization") String token, @PathVariable String id) {
        return employeeService.getEmployeeById(id);
    }

    /**
     * Deletes a Employee by ID from the database.
     *
     * @param token the authorization token for the request.
     * @param id    the ID of the Employee to delete.
     * @return a ResponseEntity containing the ResponseDTO with the result of the operation and an HTTP status code.
     */
    @DeleteMapping("/deleteById/{id}")
    public ResponseEntity<ResponseDTO> deleteById(@RequestHeader(name = "Authorization") String token, @PathVariable String id) {
        return employeeService.deleteEmployeeById(id);
    }
}
