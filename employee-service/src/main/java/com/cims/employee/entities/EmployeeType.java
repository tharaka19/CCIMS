package com.cims.employee.entities;

import com.cims.employee.constants.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.util.Set;

/**
 * Entity class for holding employee type information.
 */
@Entity
@Table(name = "employee_type")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeType {

    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "snowflake")
    @GenericGenerator(name = "snowflake", strategy = "com.cims.employee.utils.SnowflakeIdGenerator")
    private Long id;

    @Column(name = "employee_type")
    private String employeeType;

    @Column(name = "description")
    private String description;

    @Column(name = "basic_pay")
    private Double basicPay;

    @Column(name = "epf")
    private Double epf;

    @Column(name = "epf_co_contribution")
    private Double epfCoContribution;

    @Column(name = "total_epf")
    private Double totalEpf;

    @Column(name = "etf")
    private Double etf;

    @Column(name = "branch_code")
    private String branchCode;

    @OneToMany(mappedBy = "employeeType")
    private Set<EmployeeProfile> employeeProfiles;

    @Column(name = "status")
    private Status status;
}
