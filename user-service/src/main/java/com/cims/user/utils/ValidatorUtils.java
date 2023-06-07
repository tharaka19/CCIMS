package com.cims.user.utils;

import com.cims.user.constants.enums.FileType;
import com.cims.user.constants.enums.Status;

import java.util.function.Predicate;

/**
 * This is a utility class that provides methods to validate input values.
 */
public class ValidatorUtils {

    /**
     * Validates that a string input value is not or empty.
     *
     * @param inputValue the input string value to be validated.
     * @return true if the input value is null or empty, false otherwise.
     */
    public static final Predicate<String> stringNullValidator = inputValue -> inputValue == null || inputValue.isEmpty() || inputValue.equals(Status.NONE.toString());

    /**
     * Validates that a string input value matches a valid status.
     *
     * @param inputStatus the input string value to be validated.
     * @return true if the input value matches the NONE status, false otherwise.
     */
    public static final Predicate<String> statusValidator = inputStatus -> inputStatus.equals(Status.NONE.toString());

    /**
     * Validates that a object input value is null.
     *
     * @param inputValue the input string value to be validated.
     * @return true if the input value is null empty, false otherwise.
     */
    public static final Predicate<Object> entityNullValidator = inputValue -> inputValue == null;

    /**
     * Returns a Predicate function that validates whether an input FileType matches any of the valid FileType values.
     *
     * @param inputFileType the FileType value to validate
     * @return a boolean indicating whether the inputFileType matches any valid FileType values
     */
    public static final Predicate<FileType> fileTypeValidator = inputFileType -> {
        for (FileType fileType : FileType.values()) {
            if (fileType.equals(inputFileType)) {
                return true;
            }
        }
        return false;
    };
}
