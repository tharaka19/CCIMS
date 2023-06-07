package com.cims.project.dtos;

import com.cims.project.constants.enums.Operation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Data class for client project equipment stock information.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientProjectEquipmentStockDTO {

    private String id;
    private Integer availableQuantity;
    private Operation operation;
    private Integer equipmentQuantity;
    private String stockNote;
    private LocalDate date;
    private String equipmentId;
    private String clientProjectId;
    private String equipmentStockId;
    private String projectNumber;
    private String branchCode;
}
