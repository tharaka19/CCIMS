package com.cims.employee.dtos;

import com.cims.employee.constants.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data class for employee type information.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeTypeDTO {

    private String id;
    private String employeeType;
    private String description;
    private Double basicPay;
    private Double epf;
    private Double epfCoContribution;
    private Double totalEpf;
    private Double etf;
    private String branchCode;
    private Status status;
}
