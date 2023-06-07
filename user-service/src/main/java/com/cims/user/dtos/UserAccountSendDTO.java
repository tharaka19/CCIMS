package com.cims.user.dtos;

import com.cims.user.constants.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data class for holding user account information.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAccountSendDTO {

    private String id;
    private String userName;
    private String password;
    private String confirmPassword;
    private String branchCode;
    private String userRoleId;
    private Status status;
}
