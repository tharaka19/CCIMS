package com.cims.user.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data class for holding response data that is returned by the API endpoints.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseDTO {

    private String code;
    private Object message;
    private Object content;
}
