package com.cims.equipment.dtos;

import com.cims.equipment.constants.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data class for equipment information.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EquipmentDTO {

    private String id;
    private String equipmentNumber;
    private String equipmentName;
    private String equipmentTypeId;
    private String equipmentType;
    private String branchCode;
    private Status status;
}
