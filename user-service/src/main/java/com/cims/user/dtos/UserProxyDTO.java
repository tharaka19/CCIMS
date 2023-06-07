package com.cims.user.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data class for holding user proxy information.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProxyDTO {
    private String branchName;
    private String branchCode;
    private UserAccountSendDTO userAccountSendDTO;
    private UserRoleDTO userRoleDTO;
}
