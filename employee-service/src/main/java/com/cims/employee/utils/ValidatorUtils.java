package com.cims.employee.utils;

import com.cims.employee.constants.enums.Status;

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

    /**
     * validates a given string to ensure it only contains numeric values.
     *
     * @param inputNumber the string to be validated
     * @return true if the inputNumber only contains numeric values, false otherwise
     */
    public static final Predicate<String> numericValidator = inputNumber -> inputNumber.matches("-?\\d+(\\.\\d+)?");

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

    /**
     * Validates a NIC number string to ensure that it consists of valid NIC.
     *
     * @param inputNic The NIC number string to be validated.
     * @return True if the NIC number is valid, false otherwise.
     */
    public static final Predicate<String> nicValidator = inputNic -> inputNic.matches("^([0-9]{9}[x|X|v|V]|[0-9]{12})$");
}
