package com.cims.equipment.entities;

import com.cims.equipment.constants.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.util.Set;

/**
 * Entity class for holding equipment stock information.
 */
@Entity
@Table(name = "equipment_stock")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EquipmentStock {

    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "snowflake")
    @GenericGenerator(name = "snowflake", strategy = "com.cims.equipment.utils.SnowflakeIdGenerator")
    private Long id;

    @Column(name = "stockNumber")
    private String stockNumber;

    @Column(name = "purchasePrice")
    private Double purchasePrice;

    @Column(name = "available_quantity")
    private Integer availableQuantity;

    @ManyToOne
    @JoinColumn(name = "equipment_id", nullable = false)
    private Equipment equipment;

    @ManyToOne
    @JoinColumn(name = "equipment_supplier_id")
    private EquipmentSupplier equipmentSupplier;

    @OneToMany(mappedBy = "equipmentStock")
    private Set<EquipmentStockHistory> equipmentStockHistories;

    @Column(name = "branch_code")
    private String branchCode;

    @Column(name = "status")
    private Status status;
}
