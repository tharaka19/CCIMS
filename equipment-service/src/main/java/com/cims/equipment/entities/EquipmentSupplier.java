package com.cims.equipment.entities;

import com.cims.equipment.constants.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.util.Set;

/**
 * Entity class for holding equipment supplier information.
 */
@Entity
@Table(name = "equipment_supplier")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EquipmentSupplier {

    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "snowflake")
    @GenericGenerator(name = "snowflake", strategy = "com.cims.equipment.utils.SnowflakeIdGenerator")
    private Long id;

    @Column(name = "equipment_type")
    private String supplierNumber;

    @Column(name = "description")
    private String supplierName;

    @Column(name = "mobile_number")
    private String mobileNumber;

    @Column(name = "land_number")
    private String landNumber;

    @Column(name = "address")
    private String address;

    @OneToMany(mappedBy = "equipmentSupplier")
    private Set<EquipmentStock> equipmentStocks;

    @Column(name = "branch_code")
    private String branchCode;

    @Column(name = "status")
    private Status status;
}
