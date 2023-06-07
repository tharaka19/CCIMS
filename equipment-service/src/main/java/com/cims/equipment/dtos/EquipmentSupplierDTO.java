package com.cims.equipment.dtos;

import com.cims.equipment.constants.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data class for equipment supplier information.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EquipmentSupplierDTO {

    private String id;
    private String supplierNumber;
    private String supplierName;
    private String mobileNumber;
    private String landNumber;
    private String address;
    private String branchCode;
    private Status status;
}
