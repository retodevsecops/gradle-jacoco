package com.consubanco.model.commons.util;

import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@UtilityClass
public class DateUtil {

    private static final String DATE_PATTERN = "dd/MMM/yyyy";
    private static final String DATE_FORMAT = "%s/%s/%s";

    public static LocalDate stringToDate(String date) {
        String[] parts = date.split("/");
        String day = parts[0];
        String month = parts[1].substring(0, 1).toUpperCase() + parts[1].substring(1).toLowerCase();
        String year = parts[2];
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_PATTERN, Locale.ENGLISH);
        return LocalDate.parse(String.format(DATE_FORMAT, day, month, year), formatter);
    }

}