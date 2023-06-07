package com.cims.equipment.dtos;

import com.cims.equipment.constants.enums.Operation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Data class for equipment stock history information.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EquipmentStockHistoryDTO {

    private String id;
    private Integer availableQuantity;
    private Operation operation;
    private Integer equipmentQuantity;
    private String historyNote;
    private LocalDate date;
    private String equipmentStockId;
    private String stockNumber;
    private String branchCode;
}
