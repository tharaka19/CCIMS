package com.cims.employee.entities;

import com.cims.employee.constants.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.util.ArrayList;
import java.util.List;

/**
 * Entity class for holding employee profile information.
 */
@Entity
@Table(name = "employee_profile")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeProfile {

    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "snowflake")
    @GenericGenerator(name = "snowflake", strategy = "com.cims.employee.utils.SnowflakeIdGenerator")
    private Long id;

    @Column(name = "branch_code")
    private String branchCode;

    @ManyToOne
    @JoinColumn(name = "financial_year_id", nullable = false)
    private FinancialYear financialYear;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @OneToOne
    @JoinColumn(name = "employee_type_id", nullable = false)
    private EmployeeType employeeType;

    @ManyToMany
    private List<AllowanceType> allowanceTypes = new ArrayList<AllowanceType>();;

    @Column(name = "status")
    private Status status;
}
