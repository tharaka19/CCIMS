package com.cims.equipment.dtos;

import com.cims.equipment.constants.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data class for holding user account response information.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAccountResponseDTO {

    private String id;
    private String userName;
    private String branchCode;
    private String userRoleId;
    private Status status;
}
