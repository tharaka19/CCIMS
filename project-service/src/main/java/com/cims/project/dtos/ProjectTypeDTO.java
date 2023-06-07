package com.cims.project.dtos;

import com.cims.project.constants.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data class for equipment type information.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectTypeDTO {

    private String id;
    private String projectType;
    private String description;
    private String branchCode;
    private Status status;
}
