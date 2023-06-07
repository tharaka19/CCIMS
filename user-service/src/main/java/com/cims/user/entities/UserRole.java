package com.cims.user.entities;

import com.cims.user.constants.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

/**
 * Entity class for holding user role information.
 */
@Entity
@Table(name = "user_role", uniqueConstraints = {@UniqueConstraint(columnNames = {"role_name"})})
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRole {

    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "snowflake")
    @GenericGenerator(name = "snowflake", strategy = "com.cims.user.utils.SnowflakeIdGenerator")
    private Long id;

    @Column(name = "role_name", nullable = false)
    private String roleName;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "branch_name")
    private String branchName;

    @Column(name = "branch_code")
    private String branchCode;

    @Column(name = "admin_role_id")
    private Long adminRoleId;

    @Column(name = "status")
    private Status status;
}


