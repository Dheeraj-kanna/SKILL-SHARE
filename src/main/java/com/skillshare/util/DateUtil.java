package com.skillshare.util;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Date and time formatting and parsing utilities.
 */
public class DateUtil {
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    public static final DateTimeFormatter FRIENDLY_DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy");
    public static final DateTimeFormatter FRIENDLY_TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm a");

    public static Date parseSqlDate(String dateStr) throws DateTimeParseException {
        LocalDate ld = LocalDate.parse(dateStr.trim(), DATE_FORMATTER);
        return Date.valueOf(ld);
    }

    public static Time parseSqlTime(String timeStr) throws DateTimeParseException {
        String trimmed = timeStr.trim();
        // Support HH:mm or HH:mm:ss
        if (trimmed.length() == 5) {
            trimmed = trimmed + ":00";
        }
        LocalTime lt = LocalTime.parse(trimmed, TIME_FORMATTER);
        return Time.valueOf(lt);
    }

    public static String formatFriendlyDate(Date date) {
        if (date == null) return "N/A";
        return date.toLocalDate().format(FRIENDLY_DATE_FORMATTER);
    }

    public static String formatFriendlyTime(Time time) {
        if (time == null) return "N/A";
        return time.toLocalTime().format(FRIENDLY_TIME_FORMATTER);
    }
}
