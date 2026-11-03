package com.skillshare.util;

import java.util.regex.Pattern;

/**
 * Validation utilities for user input throughout the application.
 */
public class ValidationUtil {
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$"
    );

    public static boolean isValidEmail(String email) {
        if (email == null) return false;
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 4;
    }

    public static boolean isValidRating(int rating) {
        return rating >= 1 && rating <= 5;
    }
}
