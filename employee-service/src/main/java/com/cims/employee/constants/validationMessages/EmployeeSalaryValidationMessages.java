package com.cims.employee.constants.validationMessages;

/**
 * The {@code EmployeeSalaryValidationMessages} class contains constants for employee salary validation messages.
 * These messages are used by the application to validate employee type information provided by the user.
 * The constants are used as keys to retrieve the corresponding message from the resource bundle and
 * display them to the user as feedback for validation errors.
 */
public class EmployeeSalaryValidationMessages {
    public static final String EMPTY_FINANCIAL_YEAR = "Please select a financial year.";
    public static final String EMPTY_EMPLOYEE = "Please select an employee.";
    public static final String EMPTY_SALARY_MONTH = "Please select a salary month.";
    public static final String INVALID_TOTAL_OT = "Invalid total ot.";
    public static final String EXIT_SALARY_MONTH = "Salary month already exists.";
}
