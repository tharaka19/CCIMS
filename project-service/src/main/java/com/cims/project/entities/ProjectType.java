package com.cims.project.entities;

import com.cims.project.constants.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.util.Set;

/**
 * Entity class for holding project type information.
 */
@Entity
@Table(name = "project_type")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectType {

    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "snowflake")
    @GenericGenerator(name = "snowflake", strategy = "com.cims.project.utils.SnowflakeIdGenerator")
    private Long id;

    @Column(name = "project_type")
    private String projectType;

    @Column(name = "description")
    private String description;

    @OneToMany(mappedBy = "projectType")
    private Set<Project> projects;

    @Column(name = "branch_code")
    private String branchCode;

    @Column(name = "status")
    private Status status;
}
