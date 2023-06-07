package com.cims.user.entities;

import com.cims.user.constants.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

/**
 * Entity class for holding user account information.
 */
@Entity
@Table(name = "user_account", uniqueConstraints = {@UniqueConstraint(columnNames = {"user_name"})})
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAccount {

    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "snowflake")
    @GenericGenerator(name = "snowflake", strategy = "com.cims.user.utils.SnowflakeIdGenerator")
    private Long id;

    @Column(name = "user_name", length = 100, nullable = false)
    private String userName;

    @Column(name = "password", length = 128)
    private String password;

    @Column(name = "token")
    private String token;

    @Column(name = "branch_code", nullable = false)
    private String branchCode;

    @Column(name = "admin_user_id")
    private Long adminUserId;

    @ManyToOne
    @JoinColumn(name = "user_role_id")
    private UserRole userRole;

    @Column(name = "status")
    private Status status;
}
