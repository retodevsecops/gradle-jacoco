package com.consubanco.model.commons.util;

import com.consubanco.model.commons.exception.factory.ExceptionFactory;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.consubanco.model.entities.ocr.message.OcrBusinessMessage.INVALID_MONTH;

@UtilityClass
public class DateUtil {

    private static final Locale LANGUAGE = Locale.forLanguageTag("es");
    private static final String DATE_PATTERN = "dd/MM/yyyy";
    private static final String DATE_FORMAT = "%s/%s/%s";
    private static final Map<String, String> MONTHS = getMonths();

    public static LocalDate stringToDate(String date) {
        String[] parts = date.split("/");
        String day = parts[0];
        String month = findMonth(parts[1]);
        String year = parts[2];
        String formattedDate = String.format(DATE_FORMAT, day, month, year);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_PATTERN, LANGUAGE);
        return LocalDate.parse(formattedDate, formatter);
    }

    private static Map<String, String> getMonths() {
        Map<String, String> months = new HashMap<>();
        months.put("Ene", "01");
        months.put("Feb", "02");
        months.put("Mar", "03");
        months.put("Abr", "04");
        months.put("May", "05");
        months.put("Jun", "06");
        months.put("Jul", "07");
        months.put("Ago", "08");
        months.put("Sep", "09");
        months.put("Oct", "10");
        months.put("Nov", "11");
        months.put("Dic", "12");
        return months;
    }

    private static String findMonth(String abbreviatedNameMonth) {
        return MONTHS.entrySet()
                .stream()
                .filter(month -> month.getKey().equalsIgnoreCase(abbreviatedNameMonth))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElseThrow(() -> ExceptionFactory.buildBusiness(abbreviatedNameMonth, INVALID_MONTH));
    }

}