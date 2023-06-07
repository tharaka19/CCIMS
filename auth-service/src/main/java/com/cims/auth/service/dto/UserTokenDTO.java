package com.cims.auth.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data class for holding user token information.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserTokenDTO {

    private String userName;
    private String token;
}
