package com.cims.project.utils;

import com.cims.project.constants.enums.Status;

import java.time.LocalDate;
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
     * validates a given LocalDate object to ensure it is not null.
     *
     * @param inputDate the LocalDate object to be validated
     * @return true if the inputDate is null, false otherwise
     */
    public static final Predicate<LocalDate> dateValidator = inputDate -> inputDate == null;

    public static final Predicate<Integer> numericNullValidator = inputNumber -> inputNumber == null;

    /**
     * validates a given double value to ensure it is not null.
     *
     * @param inputDate the double to be validated
     * @return true if the inputPrice is null, false otherwise
     */
    public static final Predicate<Double> priceNullValidator = inputPrice -> inputPrice == null;

    /**
     * validates that tests whether a given Double value represents a valid price.
     * A price is considered valid if it is greater than zero, not NaN or infinite,
     * and has at most two decimal places.
     *
     * @param inputPrice The Double value to test.
     * @return True if the inputPrice represents a valid price, false otherwise.
     */
    public static final Predicate<Double> priceValidator = inputPrice -> inputPrice > 0 && !Double.isNaN(inputPrice) && !Double.isInfinite(inputPrice) && Math.floor(inputPrice * 100) == inputPrice * 100;

    /**
     * Validates a phone number string to ensure that it consists of exactly 10 digits.
     *
     * @param inputPhoneNumber The phone number string to be validated.
     * @return True if the phone number is valid, false otherwise.
     */
    public static final Predicate<String> phoneNumberValidator = inputPhoneNumber -> inputPhoneNumber.matches("\\d{10}");
}
