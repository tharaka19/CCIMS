package com.cims.employee.dtos;

import com.cims.employee.constants.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data class for employee history information.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeHistoryDTO {

    private String id;
    private String fileName;
    private String caption;
    private String reminder;
    private String financialYearId;
    private String financialYear;
    private String employeeId;
    private String employee;
    private String employeeFullName;
    private String branchCode;
    private Status status;
}
