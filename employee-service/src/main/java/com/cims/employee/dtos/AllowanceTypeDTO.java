package com.cims.employee.dtos;

import com.cims.employee.constants.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data class for allowance type information.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AllowanceTypeDTO {

    private String id;
    private String allowanceType;
    private String description;
    private Double allowancePay;
    private String branchCode;
    private Status status;
}
