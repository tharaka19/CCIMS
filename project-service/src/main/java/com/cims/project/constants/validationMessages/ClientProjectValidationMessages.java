package com.cims.project.constants.validationMessages;

/**
 * The {@code ClientProjectValidationMessages} class contains constants for client project validation messages.
 * These messages are used by the application to validate project information provided by the user.
 * The constants are used as keys to retrieve the corresponding message from the resource bundle and
 * display them to the user as feedback for validation errors.
 */
public class ClientProjectValidationMessages {
    public static final String EMPTY_PROJECT = "Please select a project.";
    public static final String EMPTY_CLIENT = "Please select a client.";
    public static final String EMPTY_PROJECT_NUMBER = "Please enter project number.";
    public static final String EMPTY_PROJECT_DETAILS = "Please enter project details.";
    public static final String EMPTY_PROJECT_START_DATE = "Please enter project start date.";
    public static final String INVALID_PROJECT_START_DATE = "Invalid project start date.";
    public static final String EXIT_PROJECT_NUMBER = "Project number already exists.";
}
