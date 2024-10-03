package com.consubanco.model.commons.util;

import com.consubanco.model.commons.exception.factory.ExceptionFactory;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.consubanco.model.entities.ocr.message.OcrBusinessMessage.INVALID_DATE_FORMAT;


@UtilityClass
public class DateUtil {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");


    public static LocalDate stringToDate(String date) {
        try {
            return LocalDate.parse(date, formatter);
        } catch (Exception e) {
            throw ExceptionFactory.buildBusiness(String.format(" date: %s does not match with expected dd/MM/yyyy format", date), INVALID_DATE_FORMAT);
        }
    }

}