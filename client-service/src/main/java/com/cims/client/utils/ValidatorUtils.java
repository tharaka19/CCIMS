package com.cims.client.utils;

import com.cims.client.constants.enums.Status;

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
     * Validates a phone number string to ensure that it consists of exactly 10 digits.
     *
     * @param inputPhoneNumber The phone number string to be validated.
     * @return True if the phone number is valid, false otherwise.
     */
    public static final Predicate<String> phoneNumberValidator = inputPhoneNumber -> inputPhoneNumber.matches("\\d{10}");

    /**
     * Validates a NIC number string to ensure that it consists of valid NIC.
     *
     * @param inputNic The NIC number string to be validated.
     * @return True if the NIC number is valid, false otherwise.
     */
    public static final Predicate<String> nicValidator = inputNic -> inputNic.matches("^([0-9]{9}[x|X|v|V]|[0-9]{12})$");
}
