package com.cims.employee.entities;

import com.cims.employee.constants.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDate;
import java.util.Set;

/**
 * Entity class for holding financial year information.
 */
@Entity
@Table(name = "financial_year")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FinancialYear {

    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "snowflake")
    @GenericGenerator(name = "snowflake", strategy = "com.cims.employee.utils.SnowflakeIdGenerator")
    private Long id;

    @Column(name = "financial_year")
    private String financialYear;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "branch_code")
    private String branchCode;

    @OneToMany(mappedBy = "financialYear")
    private Set<EmployeeProfile> employeeProfiles;

    @OneToMany(mappedBy = "financialYear")
    private Set<EmployeeHistory> employeeHistories;

    @OneToMany(mappedBy = "financialYear")
    private Set<EmployeeSalary> employeeSalaries;

    @Column(name = "status")
    private Status status;
}
