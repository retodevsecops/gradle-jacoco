package com.consubanco.model.entities.ocr.util;

import com.consubanco.model.commons.util.DateUtil;
import com.consubanco.model.commons.util.FortnightDates;
import com.consubanco.model.commons.util.MonthlyDates;
import com.consubanco.model.entities.ocr.OcrDocument;
import com.consubanco.model.entities.ocr.vo.OcrDataVO;
import com.consubanco.model.entities.ocr.vo.OcrUpdateVO;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static com.consubanco.model.entities.ocr.constant.OcrFailureReason.*;
import static com.consubanco.model.entities.ocr.message.OcrMessage.*;

@UtilityClass
public class PeriodicityValidatorUtil {

    public OcrUpdateVO validatePeriodicity(OcrDocument ocrDocument, List<OcrDataVO> ocrDataList, OcrDataVO initialPeriod, OcrDataVO finalPeriod, Integer daysRange) {
        LocalDate initialDate = DateUtil.stringToDate(initialPeriod.getValue());
        LocalDate finalDate = DateUtil.stringToDate(finalPeriod.getValue());
        long daysBetween = ChronoUnit.DAYS.between(initialDate, finalDate);
        if (periodicityIsMonthly(daysBetween)) {
            return monthlyValidation(ocrDocument, ocrDataList, initialDate, finalDate);
        } else if (periodicityIsFortnightly(daysBetween)) {
            return fortnightValidation(ocrDocument, ocrDataList, initialDate, finalDate, daysRange);
        }
        String reason = unknownPeriodicity(initialDate, finalDate, daysBetween);
        return new OcrUpdateVO(ocrDocument.getId(), ocrDataList, UNKNOWN_PERIODICITY, reason);
    }
    public OcrUpdateVO validateAddressValidity(OcrDocument ocrDocument, List<OcrDataVO> ocrDataList, OcrDataVO validityDate, int validityMonths) {
        LocalDate validity = DateUtil.stringToDate(validityDate.getValue());
        LocalDate now = LocalDate.now();
        LocalDate xMonthsAgo = now.minus(validityMonths, ChronoUnit.MONTHS);

        if ((validity.isAfter(xMonthsAgo) || validity.isEqual(xMonthsAgo)) && validity.isBefore(now)) {
            return new OcrUpdateVO(ocrDocument.getId(), ocrDataList);
        } else {
            String reason = expiredAddressValidity(validity, validityMonths);
            return new OcrUpdateVO(ocrDocument.getId(), ocrDataList, ADDRESS_VALIDITY_EXPIRED, reason);
        }

    }

        private static OcrUpdateVO monthlyValidation(OcrDocument ocrDocument, List<OcrDataVO> ocrDataList, LocalDate initialDate, LocalDate finalDate) {
        LocalDate[] monthlyDates = MonthlyDates.getDatesFromIndex(ocrDocument.getDocumentIndex());
        if (isDateWithinPeriod(monthlyDates, initialDate, finalDate)) {
            return new OcrUpdateVO(ocrDocument.getId(), ocrDataList);
        }
        String reason = invalidMonthlyPayStub(monthlyDates[0], monthlyDates[1], initialDate, finalDate);
        return new OcrUpdateVO(ocrDocument.getId(), ocrDataList, INVALID_DATE, reason);
    }

    private static OcrUpdateVO fortnightValidation(OcrDocument ocrDocument, List<OcrDataVO> ocrDataList, LocalDate initialDate, LocalDate finalDate, Integer daysRange) {
        int index = ocrDocument.getDocumentIndex();
        LocalDate[] fortnightDates = FortnightDates.getDatesFromIndex(index, daysRange);
        if (isDateWithinPeriod(fortnightDates, initialDate, finalDate)) {
            return new OcrUpdateVO(ocrDocument.getId(), ocrDataList);
        }
        String reason = invalidFortnightPayStub(fortnightDates[0], fortnightDates[1], initialDate, finalDate);
        return new OcrUpdateVO(ocrDocument.getId(), ocrDataList, INVALID_DATE, reason);
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