package com.cims.employee.dtos;

import com.cims.employee.constants.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Data class for financial year information.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FinancialYearDTO {

    private String id;
    private String financialYear;
    private LocalDate startDate;
    private LocalDate endDate;
    private String branchCode;
    private Status status;
}
