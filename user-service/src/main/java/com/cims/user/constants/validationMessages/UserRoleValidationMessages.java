package com.cims.user.constants.validationMessages;

/**
 * The {@code UserRoleValidationMessages} class contains constants for user role validation messages.
 * These messages are used by the application to validate user role information provided by the user.
 * The constants are used as keys to retrieve the corresponding message from the resource bundle and
 * display them to the user as feedback for validation errors.
 */
public class UserRoleValidationMessages {
    public static final String EMPTY_USER_ROLE_NAME = "Please enter user role name.";
    public static final String EMPTY_USER_ROLE_DESCRIPTION = "Please enter user role description.";
    public static final String EXIT_USER_ROLE = "User role already exists.";
    public static final String USER_ROLE_USED = "User role has active user accounts. Unable to delete.";
}
