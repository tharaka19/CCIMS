package com.cims.employee.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data class for holding response data that is returned by the Pdf.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PdfResponseDTO {

    private byte[] byteArray;
    private String type;
    private String fileName;
    private String filePath;
}
