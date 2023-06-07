package com.cims.employee.dtos;

import com.cims.employee.constants.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * Data class for employee profile information.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeProfileDTO {

    private String id;
    private String financialYearId;
    private String financialYear;
    private String employeeId;
    private String employee;
    private String employeeFullName;
    private String employeeTypeId;
    private String employeeType;
    private Set<AllowanceTypeDTO> allowanceTypes;
    private String branchCode;
    private Status status;
}
