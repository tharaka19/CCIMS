package com.cims.employee.entities;

import com.cims.employee.constants.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "employee_history")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeHistory {

    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "snowflake")
    @GenericGenerator(name = "snowflake", strategy = "com.cims.employee.utils.SnowflakeIdGenerator")
    private Long id;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "caption")
    private String caption;

    @Column(name = "reminder")
    private String reminder;

    @Column(name = "branch_code")
    private String branchCode;

    @ManyToOne
    @JoinColumn(name = "financial_year_id", nullable = false)
    private FinancialYear financialYear;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "status")
    private Status status;
}
