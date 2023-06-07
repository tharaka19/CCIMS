package com.cims.user.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * A utility class for password-related operations, such as generating and encoding passwords
 * using the BCrypt hashing algorithm.
 */
public class PasswordUtils {

    /**
     * Returns a BCrypt-hashed representation of the given raw password string.
     *
     * @param rawPassword the raw password string to be encoded
     * @return a BCrypt-hashed representation of the raw password string
     */
    public static String getBCryptPassword(String rawPassword) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        return bCryptPasswordEncoder.encode(rawPassword);
    }
}

