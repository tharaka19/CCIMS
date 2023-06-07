package com.cims.equipment.dtos;

import com.cims.equipment.constants.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data class for equipment type information.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EquipmentTypeDTO {

    private String id;
    private String equipmentType;
    private String description;
    private String branchCode;
    private Status status;
}
