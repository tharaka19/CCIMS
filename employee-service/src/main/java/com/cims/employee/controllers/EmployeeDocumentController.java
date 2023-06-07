package com.cims.employee.controllers;

import com.cims.employee.dtos.EmployeeDocumentDTO;
import com.cims.employee.services.EmployeeDocumentService;
import com.cims.employee.utils.ResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * The EmployeeDocumentController class handles HTTP requests for Employee Document management endpoints.
 */
@RestController
@RequestMapping("/employee/employeeDocument")
public class EmployeeDocumentController {

    @Autowired
    private EmployeeDocumentService employeeDocumentService;

    /**
     * Saves or updates a Employee Document in the database.
     *
     * @param token           the authorization token for the request.
     * @param employeeDocumentDTO the DTO representing the Employee Document to be saved or updated.
     * @return a ResponseEntity containing the ResponseDTO with the result of the operation and an HTTP status code.
     */
    @PostMapping("/saveUpdate")
    public ResponseEntity<ResponseDTO> saveUpdate(@RequestHeader(name = "Authorization") String token, @RequestBody EmployeeDocumentDTO employeeDocumentDTO) {
        return employeeDocumentService.saveUpdateEmployeeDocument(token, employeeDocumentDTO);
    }

    /**
     * Gets all Employee Documents from the database.
     *
     * @param token the authorization token for the request.
     * @return a ResponseEntity containing the ResponseDTO with the list of Employee Document DTOs and an HTTP status code.
     */
    @GetMapping("/getAll")
    public ResponseEntity<ResponseDTO> getAll(@RequestHeader(name = "Authorization") String token) {
        return employeeDocumentService.getAllEmployeeDocuments(token);
    }

    /**
     * Gets all active Employee Documents from the database.
     *
     * @param token the authorization token for the request.
     * @return a ResponseEntity containing the ResponseDTO with the list of active Employee Document DTOs and an HTTP status code.
     */
    @GetMapping("/getAllActive")
    public ResponseEntity<ResponseDTO> getAllActive(@RequestHeader(name = "Authorization") String token) {
        return employeeDocumentService.getAllActiveEmployeeDocuments(token);
    }

    /**
     * Gets a Employee Document by ID from the database.
     *
     * @param token the authorization token for the request.
     * @param id    the ID of the Employee Document to retrieve.
     * @return a ResponseEntity containing the ResponseDTO with the Employee Document DTO and an HTTP status code.
     */
    @GetMapping("/getById/{id}")
    public ResponseEntity<ResponseDTO> getById(@RequestHeader(name = "Authorization") String token, @PathVariable String id) {
        return employeeDocumentService.getEmployeeDocumentById(id);
    }

    /**
     * Deletes a Employee Document by ID from the database.
     *
     * @param token the authorization token for the request.
     * @param id    the ID of the Employee Document to delete.
     * @return a ResponseEntity containing the ResponseDTO with the result of the operation and an HTTP status code.
     */
    @DeleteMapping("/deleteById/{id}")
    public ResponseEntity<ResponseDTO> deleteById(@RequestHeader(name = "Authorization") String token, @PathVariable String id) {
        return employeeDocumentService.deleteEmployeeDocumentById(id);
    }
}
