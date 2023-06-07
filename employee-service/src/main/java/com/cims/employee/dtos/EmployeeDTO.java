package com.cims.employee.dtos;

import com.cims.employee.constants.enums.Gender;
import com.cims.employee.constants.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Data class for employee information.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeDTO {

    private String id;
    private String employeeNumber;
    private String nameWithInitials;
    private String firstName;
    private String lastName;
    private String fullName;
    private LocalDate dob;
    private String nic;
    private LocalDate joinDate;
    private String mobileNumber;
    private String homeNumber;
    private String homeAddress;
    private Gender gender;
    private String branchCode;
    private Status status;
}
