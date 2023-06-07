package com.cims.user.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data class for holding page redirection information.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageRedirectionDTO {

    private String userName;
    private String token;
}
