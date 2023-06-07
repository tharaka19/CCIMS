package com.cims.equipment.dtos;

import com.cims.equipment.constants.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data class for equipment stock information.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EquipmentStockDTO {

    private String id;
    private String stockNumber;
    private Double purchasePrice;
    private Integer availableQuantity;
    private String equipmentTypeId;
    private String equipmentType;
    private String equipmentId;
    private String equipmentNumber;
    private String equipmentName;
    private String equipmentSupplierId;
    private String supplierName;
    private String branchCode;
    private Status status;
}
