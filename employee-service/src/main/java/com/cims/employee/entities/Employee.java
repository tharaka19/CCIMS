package com.cims.employee.entities;

import com.cims.employee.constants.enums.Gender;
import com.cims.employee.constants.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDate;
import java.util.Set;

/**
 * Entity class for holding employee information.
 */
@Entity
@Table(name = "employee")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Employee {

    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "snowflake")
    @GenericGenerator(name = "snowflake", strategy = "com.cims.employee.utils.SnowflakeIdGenerator")
    private Long id;

    @Column(name = "employee_number")
    private String employeeNumber;

    @Column(name = "name_with_initials")
    private String nameWithInitials;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "dob")
    private LocalDate dob;

    @Column(name = "nic")
    private String nic;

    @Column(name = "join_date")
    private LocalDate joinDate;

    @Column(name = "mobile_number")
    private String mobileNumber;

    @Column(name = "home_number")
    private String homeNumber;

    @Column(name = "home_address")
    private String homeAddress;

    @Column(name = "gender")
    private Gender gender;

    @Column(name = "branch_code")
    private String branchCode;

    @OneToMany(mappedBy = "employee")
    private Set<EmployeeProfile> employeeProfiles;

    @OneToMany(mappedBy = "employee")
    private Set<EmployeeDocument> employeeDocuments;

    @OneToMany(mappedBy = "employee")
    private Set<EmployeeHistory> employeeHistories;

    @OneToMany(mappedBy = "employee")
    private Set<EmployeeSalary> employeeSalaries;

    @Column(name = "status")
    private Status status;
}
