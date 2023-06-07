package com.cims.employee.dtos;

import com.cims.employee.constants.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data class for document type information.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocumentTypeDTO {

    private String id;
    private String documentType;
    private String description;
    private String branchCode;
    private Status status;
}
