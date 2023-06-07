package com.cims.employee.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * A utility class for creating and managing response objects.
 */
@Component
public class ResponseUtils {

    /**
     * Creates a new response object with the specified code, message, content, and HTTP status.
     *
     * @param code       the response code to be set
     * @param message    the response message to be set
     * @param content    the response content to be set
     * @param httpStatus the HTTP status to be set
     * @return a new ResponseEntity object containing the response object and the specified HTTP status
     */
    public ResponseEntity<ResponseDTO> createResponseDTO(String code, Object message, Object content, HttpStatus httpStatus) {
        ResponseDTO responseDTO = new ResponseDTO(code, message, content);
        return new ResponseEntity<>(responseDTO, httpStatus);
    }
}
