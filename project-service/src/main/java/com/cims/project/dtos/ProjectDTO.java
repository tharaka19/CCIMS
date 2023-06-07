package com.cims.project.dtos;

import com.cims.project.constants.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data class for project information.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectDTO {

    private String id;
    private String projectNumber;
    private String projectName;
    private String projectDetails;
    private Double projectPrice;
    private String fileName;
    private String projectTypeId;
    private String projectType;
    private String branchCode;
    private Status status;
}
