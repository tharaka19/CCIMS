package com.cims.project.entities;

import com.cims.project.constants.enums.Operation;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDate;

/**
 * Entity class for holding client project equipment stock information.
 */
@Entity
@Table(name = "client_project_equipment_stock")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientProjectEquipmentStock {

    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "snowflake")
    @GenericGenerator(name = "snowflake", strategy = "com.cims.project.utils.SnowflakeIdGenerator")
    private Long id;

    @Column(name = "operation")
    private Operation operation;

    @Column(name = "equipment_quantity")
    private Integer equipmentQuantity;

    @Column(name = "stock_note")
    private String stockNote;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "equipment_id")
    private Long equipmentId;

    @ManyToOne
    @JoinColumn(name = "client_project_id", nullable = false)
    private ClientProject clientProject;

    @Column(name = "branch_code")
    private String branchCode;
}
