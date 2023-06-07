package com.cims.employee.controllers;

import com.cims.employee.dtos.EmployeeSalaryDTO;
import com.cims.employee.services.EmployeeSalaryService;
import com.cims.employee.utils.PdfResponseDTO;
import com.cims.employee.utils.ResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * The EmployeeSalaryController class handles HTTP requests for Employee Salary management endpoints.
 */
@RestController
@RequestMapping("/employee/employeeSalary")
public class EmployeeSalaryController {

    @Autowired
    private EmployeeSalaryService employeeSalaryService;

    /**
     * Saves or updates a Employee Salary in the database.
     *
     * @param token             the authorization token for the request.
     * @param employeeSalaryDTO the DTO representing the Employee Salary to be saved or updated.
     * @return a ResponseEntity containing the ResponseDTO with the result of the operation and an HTTP status code.
     */
    @PostMapping("/saveUpdate")
    public ResponseEntity<ResponseDTO> saveUpdate(@RequestHeader(name = "Authorization") String token, @RequestBody EmployeeSalaryDTO employeeSalaryDTO) {
        return employeeSalaryService.saveUpdateEmployeeSalary(token, employeeSalaryDTO);
    }

    /**
     * Gets all Employee Salarys from the database.
     *
     * @param token the authorization token for the request.
     * @return a ResponseEntity containing the ResponseDTO with the list of Employee Salary DTOs and an HTTP status code.
     */
    @GetMapping("/getAll")
    public ResponseEntity<ResponseDTO> getAll(@RequestHeader(name = "Authorization") String token) {
        return employeeSalaryService.getAllEmployeeSalaries(token);
    }

    /**
     * Gets a Employee Salary by ID from the database.
     *
     * @param token the authorization token for the request.
     * @param id    the ID of the Employee Salary to retrieve.
     * @return a ResponseEntity containing the ResponseDTO with the Employee Salary DTO and an HTTP status code.
     */
    @GetMapping("/getById/{id}")
    public ResponseEntity<ResponseDTO> getById(@RequestHeader(name = "Authorization") String token, @PathVariable String id) {
        return employeeSalaryService.getEmployeeSalaryById(id);
    }

    /**
     * Deletes a Employee Salary by ID from the database.
     *
     * @param token the authorization token for the request.
     * @param id    the ID of the Employee Salary to delete.
     * @return a ResponseEntity containing the ResponseDTO with the result of the operation and an HTTP status code.
     */
    @DeleteMapping("/deleteById/{id}")
    public ResponseEntity<ResponseDTO> deleteById(@RequestHeader(name = "Authorization") String token, @PathVariable String id) {
        return employeeSalaryService.deleteEmployeeSalaryById(id);
    }

    /**
     * This method is an endpoint for exporting the payslip of an employee.
     * It takes in an employee ID as a path variable and an Authorization token in the header.
     * It calls the exportPaySlip method of the EmployeeSalaryService to generate a List of PdfResponseDTO objects
     * containing the payslip PDFs byte array and file path, and returns it as a ResponseEntity with a status of OK.
     *
     * @param token The Authorization token.
     * @param id    The ID of the employee whose payslip is to be exported.
     * @return A ResponseEntity containing a List of PdfResponseDTO objects representing the payslip PDFs byte array and file path.
     */
    @GetMapping("/exportPaySlip/{id}")
    public ResponseEntity<List<PdfResponseDTO>> exportPaymentReceipt(@RequestHeader(name = "Authorization") String token, @PathVariable String id) {
        return new ResponseEntity<>(employeeSalaryService.exportPaySlip(id), HttpStatus.OK);
    }
}
