package com.cims.user.utils;

/**
 * This class provides utility methods for manipulating strings.
 */
public class StringUtils {

    /**
     * Extracts the file name from a string that contains both the file name and a timestamp appended to it, separated by an underscore.
     *
     * @param str the string containing the file name and timestamp
     * @return the file name extracted from the string
     */
    public static String extractFileName(String str) {
        return str.split("_")[0];
    }

    /**
     * Removes the "Bearer " prefix from a token string.
     *
     * @param token the input token string
     * @return the input token string with the "Bearer " prefix removed, or null if the input token string is null or shorter than 7 characters
     */
    public static String extractBearerPrefix(String token) {
        return token.replace("Bearer ", "");
    }
}
