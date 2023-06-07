package com.cims.employee.controllers;

import com.cims.employee.dtos.FinancialYearDTO;
import com.cims.employee.services.FinancialYearService;
import com.cims.employee.utils.ResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * The FinancialYearController class handles HTTP requests for Financial Year management endpoints.
 */
@RestController
@RequestMapping("/employee/financialYear")
public class FinancialYearController {

    @Autowired
    private FinancialYearService financialYearService;

    /**
     * Saves or updates a Financial Year in the database.
     *
     * @param token            the authorization token for the request.
     * @param financialYearDTO the DTO representing the Financial Year to be saved or updated.
     * @return a ResponseEntity containing the ResponseDTO with the result of the operation and an HTTP status code.
     */
    @PostMapping("/saveUpdate")
    public ResponseEntity<ResponseDTO> saveUpdate(@RequestHeader(name = "Authorization") String token, @RequestBody FinancialYearDTO financialYearDTO) {
        return financialYearService.saveUpdateFinancialYear(token, financialYearDTO);
    }

    /**
     * Gets all Financial Years from the database.
     *
     * @param token the authorization token for the request.
     * @return a ResponseEntity containing the ResponseDTO with the list of Financial Year DTOs and an HTTP status code.
     */
    @GetMapping("/getAll")
    public ResponseEntity<ResponseDTO> getAll(@RequestHeader(name = "Authorization") String token) {
        return financialYearService.getAllFinancialYears(token);
    }

    /**
     * Gets all active Financial Years from the database.
     *
     * @param token the authorization token for the request.
     * @return a ResponseEntity containing the ResponseDTO with the list of active Financial Year DTOs and an HTTP status code.
     */
    @GetMapping("/getAllActive")
    public ResponseEntity<ResponseDTO> getAllActive(@RequestHeader(name = "Authorization") String token) {
        return financialYearService.getAllActiveFinancialYears(token);
    }

    /**
     * Gets a Financial Year by ID from the database.
     *
     * @param token the authorization token for the request.
     * @param id    the ID of the Financial Year to retrieve.
     * @return a ResponseEntity containing the ResponseDTO with the Financial Year DTO and an HTTP status code.
     */
    @GetMapping("/getById/{id}")
    public ResponseEntity<ResponseDTO> getById(@RequestHeader(name = "Authorization") String token, @PathVariable String id) {
        return financialYearService.getFinancialYearById(id);
    }

    /**
     * Deletes a Financial Year by ID from the database.
     *
     * @param token the authorization token for the request.
     * @param id    the ID of the Financial Year to delete.
     * @return a ResponseEntity containing the ResponseDTO with the result of the operation and an HTTP status code.
     */
    @DeleteMapping("/deleteById/{id}")
    public ResponseEntity<ResponseDTO> deleteById(@RequestHeader(name = "Authorization") String token, @PathVariable String id) {
        return financialYearService.deleteFinancialYearById(id);
    }
}
