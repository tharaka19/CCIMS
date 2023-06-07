package com.cims.employee.entities;

import com.cims.employee.constants.enums.Month;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

/**
 * Entity class for holding employee salary information.
 */
@Entity
@Table(name = "employee_salary")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeSalary {

    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "snowflake")
    @GenericGenerator(name = "snowflake", strategy = "com.cims.employee.utils.SnowflakeIdGenerator")
    private Long id;

    @Column(name = "total_allowance")
    private Double totalAllowance;

    @Column(name = "total_0t")
    private Double totalOT;

    @Column(name = "salary_month")
    private Month salaryMonth;

    @Column(name = "branch_code")
    private String branchCode;

    @ManyToOne
    @JoinColumn(name = "financial_year_id", nullable = false)
    private FinancialYear financialYear;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;
}
