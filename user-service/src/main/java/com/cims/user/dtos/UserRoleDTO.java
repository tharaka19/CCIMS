package com.cims.user.dtos;

import com.cims.user.constants.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data class for holding user role information.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRoleDTO {

    private String id;
    private String roleName;
    private String description;
    private String branchName;
    private String branchCode;
    private Status status;
}
