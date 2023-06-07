package com.cims.project.entities;

import com.cims.project.constants.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.util.Set;

/**
 * Entity class for holding project information.
 */
@Entity
@Table(name = "project")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Project {

    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "snowflake")
    @GenericGenerator(name = "snowflake", strategy = "com.cims.project.utils.SnowflakeIdGenerator")
    private Long id;

    @Column(name = "project_number")
    private String projectNumber;

    @Column(name = "project_name")
    private String projectName;

    @Column(name = "project_details")
    private String projectDetails;

    @Column(name = "project_price")
    private Double projectPrice;

    @Column(name = "file_name")
    private String fileName;

    @ManyToOne
    @JoinColumn(name = "project_type_id", nullable = false)
    private ProjectType projectType;

    @OneToMany(mappedBy = "project")
    private Set<ClientProject> clientProjects;

    @Column(name = "branch_code")
    private String branchCode;

    @Column(name = "status")
    private Status status;
}
