package com.cims.user.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data class for file-pdf information.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PdfResponseDTO {

    private byte[] byteArray;
    private String type;
    private String fileName;
}
