package com.consubanco.model.entities.ocr.util;

import com.consubanco.model.commons.util.DateUtil;
import com.consubanco.model.commons.util.FortnightDates;
import com.consubanco.model.commons.util.MonthlyDates;
import com.consubanco.model.entities.ocr.OcrAnalysisResult;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static com.consubanco.model.entities.ocr.constant.OcrFailureReason.*;
import static com.consubanco.model.entities.ocr.message.OcrMessage.*;
import static com.consubanco.model.entities.ocr.util.OcrResultFactoryUtil.analysisFailed;
import static com.consubanco.model.entities.ocr.util.OcrResultFactoryUtil.analysisSuccess;

@UtilityClass
public class PeriodicityValidatorUtil {

    public OcrAnalysisResult validatePeriodicity(int index, String initialPeriod, String finalPeriod, Integer daysRange) {
        LocalDate initialDate = DateUtil.stringToDate(initialPeriod);
        LocalDate finalDate = DateUtil.stringToDate(finalPeriod);
        long daysBetween = ChronoUnit.DAYS.between(initialDate, finalDate);
        if (periodicityIsMonthly(daysBetween)) {
            return monthlyValidation(index, initialDate, finalDate);
        } else if (periodicityIsFortnightly(daysBetween)) {
            return fortnightValidation(index, initialDate, finalDate, daysRange);
        }
        String reason = unknownPeriodicity(initialDate, finalDate, daysBetween);
        return OcrResultFactoryUtil.analysisFailed(UNKNOWN_PERIODICITY, reason);
    }

    public OcrAnalysisResult validateAddressValidity(String validityDate, int validityMonths) {
        LocalDate validity = DateUtil.stringToDate(validityDate);
        LocalDate now = LocalDate.now();
        LocalDate xMonthsAgo = now.minusMonths(validityMonths);
        if ((validity.isAfter(xMonthsAgo) || validity.isEqual(xMonthsAgo)) && validity.isBefore(now)) {
            return analysisSuccess();
        }
        String reason = expiredAddressValidity(validity, validityMonths);
        return analysisFailed(ADDRESS_VALIDITY_EXPIRED, reason);
    }

    private static OcrAnalysisResult monthlyValidation(int index, LocalDate initialDate, LocalDate finalDate) {
        LocalDate[] monthlyDates = MonthlyDates.getDatesFromIndex(index);
        if (isDateWithinPeriod(monthlyDates, initialDate, finalDate)) {
            return analysisSuccess();
        }
        String reason = invalidMonthlyPayStub(monthlyDates[0], monthlyDates[1], initialDate, finalDate);
        return analysisFailed(INVALID_DATE, reason);
    }

    private static OcrAnalysisResult fortnightValidation(int index, LocalDate initialDate, LocalDate finalDate, Integer range) {
        LocalDate[] fortnightDates = FortnightDates.getDatesFromIndex(index, range);
        if (isDateWithinPeriod(fortnightDates, initialDate, finalDate)) {
            return analysisSuccess();
        }
        String reason = invalidFortnightPayStub(fortnightDates[0], fortnightDates[1], initialDate, finalDate);
        return analysisFailed(INVALID_DATE, reason);
    }

    private static boolean periodicityIsMonthly(long daysBetween) {
        return daysBetween >= 28 && daysBetween <= 31;
    }

    private static boolean periodicityIsFortnightly(long daysBetween) {
        return daysBetween >= 14 && daysBetween <= 16;
    }

    private static boolean isDateWithinPeriod(LocalDate[] validDates, LocalDate initialDate, LocalDate finalDate) {
        return (initialDate.isEqual(validDates[0]) || initialDate.isAfter(validDates[0])) &&
                (finalDate.isEqual(validDates[1]) || finalDate.isBefore(validDates[1]));
    }

}