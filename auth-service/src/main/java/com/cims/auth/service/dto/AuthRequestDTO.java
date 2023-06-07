package com.cims.auth.service.dto;

import com.cims.auth.service.constants.enums.ServiceName;
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

    private ServiceName serviceName;
    private String userName;
    private String password;
}
