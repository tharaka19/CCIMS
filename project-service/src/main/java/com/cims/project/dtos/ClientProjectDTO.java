package com.cims.project.dtos;

import com.cims.project.constants.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Data class for client project information.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientProjectDTO {

    private String id;
    private String projectNumber;
    private String projectDetails;
    private LocalDate projectStartDate;
    private String fileName;
    private String projectId;
    private String clientId;
    private String branchCode;
    private Status status;
}
