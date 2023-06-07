package com.cims.user.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data class for holding authentication request information.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthRequestDTO {

    private String userName;
    private String password;
}
