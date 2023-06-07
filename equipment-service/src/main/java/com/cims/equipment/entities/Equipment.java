package com.cims.equipment.entities;

import com.cims.equipment.constants.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.util.Set;

/**
 * Entity class for holding equipment information.
 */
@Entity
@Table(name = "equipment")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Equipment {

    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "snowflake")
    @GenericGenerator(name = "snowflake", strategy = "com.cims.equipment.utils.SnowflakeIdGenerator")
    private Long id;

    @Column(name = "equipment_number")
    private String equipmentNumber;

    @Column(name = "equipment_name")
    private String equipmentName;

    @ManyToOne
    @JoinColumn(name = "equipment_type_id", nullable = false)
    private EquipmentType equipmentType;

    @OneToMany(mappedBy = "equipment")
    private Set<EquipmentStock> equipmentStocks;

    @Column(name = "branch_code")
    private String branchCode;

    @Column(name = "status")
    private Status status;
}
