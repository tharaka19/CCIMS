package com.cims.employee.entities;

import com.cims.employee.constants.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.util.Set;

/**
 * Entity class for holding allowance type information.
 */
@Entity
@Table(name = "allowance_type")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AllowanceType {

    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "snowflake")
    @GenericGenerator(name = "snowflake", strategy = "com.cims.employee.utils.SnowflakeIdGenerator")
    private Long id;

    @Column(name = "allowance_type")
    private String allowanceType;

    @Column(name = "description")
    private String description;

    @Column(name = "allowance_pay")
    private Double allowancePay;

    @Column(name = "branch_code")
    private String branchCode;

    @ManyToMany(mappedBy = "allowanceTypes")
    private Set<EmployeeProfile> employeeProfiles;

    @Column(name = "status")
    private Status status;
}
