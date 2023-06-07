package com.cims.equipment.entities;

import com.cims.equipment.constants.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.util.Set;

/**
 * Entity class for holding equipment type information.
 */
@Entity
@Table(name = "equipment_type")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EquipmentType {

    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "snowflake")
    @GenericGenerator(name = "snowflake", strategy = "com.cims.equipment.utils.SnowflakeIdGenerator")
    private Long id;

    @Column(name = "equipment_type")
    private String equipmentType;

    @Column(name = "description")
    private String description;

    @OneToMany(mappedBy = "equipmentType")
    private Set<Equipment> equipments;

    @Column(name = "branch_code")
    private String branchCode;

    @Column(name = "status")
    private Status status;
}
