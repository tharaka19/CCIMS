package com.cims.employee.dtos;

import com.cims.employee.constants.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data class for employee document information.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeDocumentDTO {

    private String id;
    private String fileName;
    private String employeeId;
    private String employee;
    private String employeeFullName;
    private String documentTypeId;
    private String documentType;
    private String branchCode;
    private Status status;
}
