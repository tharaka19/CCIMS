package com.cims.project.constants.validationMessages;

/**
 * The {@code ProjectValidationMessages} class contains constants for project validation messages.
 * These messages are used by the application to validate project information provided by the user.
 * The constants are used as keys to retrieve the corresponding message from the resource bundle and
 * display them to the user as feedback for validation errors.
 */
public class ProjectValidationMessages {
    public static final String EMPTY_PROJECT_TYPE = "Please select a project type.";
    public static final String EMPTY_PROJECT_NUMBER = "Please enter project number.";
    public static final String EMPTY_PROJECT_NAME = "Please enter project name.";
    public static final String EMPTY_PROJECT_DETAILS = "Please enter project details.";
    public static final String EMPTY_PROJECT_PRICE = "Please enter project price.";
    public static final String INVALID_PROJECT_PRICE = "Invalid project price.";
    public static final String EXIT_PROJECT_NUMBER = "Project number already exists.";
}
