package com.cims.user.constants.enums;

/**
 * Enumeration of user roles for a system.
 * This enum represents the different roles that users can have in a system,
 * which are SUPER_ADMIN and SYSTEM_ADMIN. These roles can be used to differentiate
 * between the level of permissions and access granted to different users in the system.
 * The SUPER_ADMIN role has the highest level of access and permissions in the system,
 * while the SYSTEM_ADMIN role has a lower level of access and permissions compared to
 * the SUPER_ADMIN role but higher than other roles in the system.
 */
public enum UserRoleType {
    SUPER_ADMIN,
    SYSTEM_ADMIN
}
