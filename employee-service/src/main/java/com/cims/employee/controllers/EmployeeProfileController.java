package com.cims.employee.controllers;

import com.cims.employee.dtos.EmployeeProfileDTO;
import com.cims.employee.services.EmployeeProfileService;
import com.cims.employee.utils.ResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * The EmployeeProfileController class handles HTTP requests for Employee Profile management endpoints.
 */
@RestController
@RequestMapping("/employee/employeeProfile")
public class EmployeeProfileController {

    @Autowired
    private EmployeeProfileService employeeProfileService;

    /**
     * Saves or updates a Employee Profile in the database.
     *
     * @param token              the authorization token for the request.
     * @param employeeProfileDTO the DTO representing the Employee Profile to be saved or updated.
     * @return a ResponseEntity containing the ResponseDTO with the result of the operation and an HTTP status code.
     */
    @PostMapping("/saveUpdate")
    public ResponseEntity<ResponseDTO> saveUpdate(@RequestHeader(name = "Authorization") String token, @RequestBody EmployeeProfileDTO employeeProfileDTO) {
        return employeeProfileService.saveUpdateEmployeeProfile(token, employeeProfileDTO);
    }

    /**
     * Gets all Employee Profiles from the database.
     *
     * @param token the authorization token for the request.
     * @return a ResponseEntity containing the ResponseDTO with the list of Employee Profile DTOs and an HTTP status code.
     */
    @GetMapping("/getAll")
    public ResponseEntity<ResponseDTO> getAll(@RequestHeader(name = "Authorization") String token) {
        return employeeProfileService.getAllEmployeeProfiles(token);
    }

    /**
     * Gets all active Employee Profiles from the database.
     *
     * @param token the authorization token for the request.
     * @return a ResponseEntity containing the ResponseDTO with the list of active Employee Profile DTOs and an HTTP status code.
     */
    @GetMapping("/getAllActive")
    public ResponseEntity<ResponseDTO> getAllActive(@RequestHeader(name = "Authorization") String token) {
        return employeeProfileService.getAllActiveEmployeeProfiles(token);
    }

    /**
     * Gets all active Employee Profiles by Financial Year from the database.
     *
     * @param token the authorization token for the request.
     * @return a ResponseEntity containing the ResponseDTO with the list of active Employee Profile DTOs and an HTTP status code.
     */
    @GetMapping("/getAllActiveByFinancialYear/{financialYearId}")
    public ResponseEntity<ResponseDTO> getAllActiveByFinancialYear(@RequestHeader(name = "Authorization") String token, @PathVariable String financialYearId) {
        return employeeProfileService.getAllActiveEmployeeProfilesByFinancialYear(token, financialYearId);
    }

    /**
     * Gets a Employee Profile by ID from the database.
     *
     * @param token the authorization token for the request.
     * @param id    the ID of the Employee Profile to retrieve.
     * @return a ResponseEntity containing the ResponseDTO with the Employee Profile DTO and an HTTP status code.
     */
    @GetMapping("/getById/{id}")
    public ResponseEntity<ResponseDTO> getById(@RequestHeader(name = "Authorization") String token, @PathVariable String id) {
        return employeeProfileService.getEmployeeProfileById(id);
    }

    /**
     * Gets a Employee Profile by ID from the database.
     *
     * @param token           the authorization token for the request.
     * @param financialYearId the FinancialYear ID of the Employee Profile to retrieve.
     * @param employeeId      the Employee ID of the Employee Profile to retrieve.
     * @return a ResponseEntity containing the ResponseDTO with the Employee Profile DTO and an HTTP status code.
     */
    @GetMapping("/getByEmployeeAndFinancialYear/{financialYearId}/{employeeId}")
    public ResponseEntity<ResponseDTO> getByEmployeeAndFinancialYear(@RequestHeader(name = "Authorization") String token, @PathVariable String financialYearId, @PathVariable String employeeId) {
        return employeeProfileService.getEmployeeProfileByFinancialYearAndEmployee(financialYearId, employeeId);
    }

    /**
     * Deletes a Employee Profile by ID from the database.
     *
     * @param token the authorization token for the request.
     * @param id    the ID of the Employee Profile to delete.
     * @return a ResponseEntity containing the ResponseDTO with the result of the operation and an HTTP status code.
     */
    @DeleteMapping("/deleteById/{id}")
    public ResponseEntity<ResponseDTO> deleteById(@RequestHeader(name = "Authorization") String token, @PathVariable String id) {
        return employeeProfileService.deleteEmployeeProfileById(id);
    }
}
