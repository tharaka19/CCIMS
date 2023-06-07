package com.cims.employee.constants.validationMessages;

/**
 * The {@code FinancialYearValidationMessages} class contains constants for financial year validation messages.
 * These messages are used by the application to validate financial year information provided by the user.
 * The constants are used as keys to retrieve the corresponding message from the resource bundle and
 * display them to the user as feedback for validation errors.
 */
public class FinancialYearValidationMessages {
    public static final String EMPTY_FINANCIAL_YEAR_NAME = "Please enter financial year name.";
    public static final String EMPTY_TERM_NAME = "Please enter term name.";
    public static final String EMPTY_START_DATE = "Please input start date.";
    public static final String EMPTY_END_DATE = "Please input end date.";
    public static final String INVALID_FINANCIAL_YEAR = "Invalid financial year.";
    public static final String INVALID_DATE_RANGE = "Invalid date range.";
    public static final String EXIT_FINANCIAL_YEAR = "Financial year already exists.";
}
