package com.cims.user.constants.validationMessages;

/**
 * The {@code UserAccountValidationMessages} class contains constants for user account validation messages.
 * These messages are used by the application to validate user account information provided by the user.
 * The constants are used as keys to retrieve the corresponding message from the resource bundle and
 * display them to the user as feedback for validation errors.
 */
public class UserAccountValidationMessages {
    public static final String EMPTY_USER_ROLE = "Please select a user role.";
    public static final String EMPTY_USER_ACCOUNT_NAME = "Please enter user account name.";
    public static final String EMPTY_USER_ACCOUNT_PASSWORD = "Please enter user account password.";
    public static final String EMPTY_USER_ACCOUNT_CONFIRM_PASSWORD = "Please enter user account confirm password.";
    public static final String PASSWORD_MIS_MATCH = "Passwords are mismatched.";
    public static final String EXIT_USER_ACCOUNT = "User account already exists.";
    public static final String USER_ACCOUNT_USED = "User account has active user accounts. Unable to delete.";
}
