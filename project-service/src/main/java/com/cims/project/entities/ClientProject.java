package com.cims.project.entities;

import com.cims.project.constants.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDate;
import java.util.Set;

/**
 * Entity class for holding client project information.
 */
@Entity
@Table(name = "client_project")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientProject {

    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "snowflake")
    @GenericGenerator(name = "snowflake", strategy = "com.cims.project.utils.SnowflakeIdGenerator")
    private Long id;

    @Column(name = "project_number")
    private String projectNumber;

    @Column(name = "project_details")
    private String projectDetails;

    @Column(name = "project_start_date")
    private LocalDate projectStartDate;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "client_id")
    private Long clientId;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @OneToMany(mappedBy = "clientProject")
    private Set<ClientProjectEquipmentStock> clientProjectEquipmentStocks;

    @Column(name = "branch_code")
    private String branchCode;

    @Column(name = "status")
    private Status status;
}
