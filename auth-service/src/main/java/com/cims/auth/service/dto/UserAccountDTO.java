package com.cims.auth.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data class for holding user account information.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAccountDTO {

    private String id;
    private String userName;
    private String password;
    private String token;
    private String roleId;
}
