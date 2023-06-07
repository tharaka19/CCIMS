package com.cims.employee.dtos;

import com.cims.employee.constants.enums.Month;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data class for employee salary information.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeSalaryDTO {

    private String id;
    private Double totalAllowance;
    private Double totalOT;
    private Month salaryMonth;
    private String financialYearId;
    private String financialYear;
    private String employeeId;
    private String employee;
    private String employeeFullName;
    private String branchCode;
}
