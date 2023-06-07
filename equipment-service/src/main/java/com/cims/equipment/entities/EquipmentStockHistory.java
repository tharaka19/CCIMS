package com.cims.equipment.entities;

import com.cims.equipment.constants.enums.Operation;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDate;

/**
 * Entity class for holding equipment stock history information.
 */
@Entity
@Table(name = "equipment_stock_history")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EquipmentStockHistory {

    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "snowflake")
    @GenericGenerator(name = "snowflake", strategy = "com.cims.equipment.utils.SnowflakeIdGenerator")
    private Long id;

    @Column(name = "operation")
    private Operation operation;

    @Column(name = "equipment_quantity")
    private Integer equipmentQuantity;

    @Column(name = "history_Note")
    private String historyNote;

    @Column(name = "date")
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "equipment_stock_id", nullable = false)
    private EquipmentStock equipmentStock;

    @Column(name = "branch_code")
    private String branchCode;
}
