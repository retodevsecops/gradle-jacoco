package com.consubanco.model.commons.util;

import com.consubanco.model.commons.exception.factory.ExceptionFactory;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.consubanco.model.entities.ocr.message.OcrBusinessMessage.INVALID_DATE_FORMAT;


@UtilityClass
public class DateUtil {



    public static LocalDate stringToDate(String date) {
        try {
            return LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (Exception e) {
            throw ExceptionFactory.buildBusiness(String.format(" date: %s doesnt match with ISO format", date), INVALID_DATE_FORMAT);
        }
    }

}