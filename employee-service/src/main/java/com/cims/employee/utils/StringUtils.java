package com.cims.employee.utils;

/**
 * This class provides utility methods for manipulating strings.
 */
public class StringUtils {

    /**
     * Extracts the first part of a hyphen-separated string.
     *
     * @param str the input string
     * @return the first part of the input string
     */
    public static String extractFirstPart(String str) {
        return str.split("-")[0];
    }

    /**
     * Extracts the second part of a hyphen-separated string.
     *
     * @param str the input string
     * @return the second part of the input string
     */
    public static String extractSecondPart(String str) {
        return str.split("-")[1];
    }
}
