package com.consubanco.model.entities.ocr.util;

import com.consubanco.model.commons.util.DateUtil;
import com.consubanco.model.commons.util.FortnightDates;
import com.consubanco.model.commons.util.MonthlyDates;
import com.consubanco.model.entities.ocr.OcrDocument;
import com.consubanco.model.entities.ocr.vo.OcrDataVO;
import com.consubanco.model.entities.ocr.vo.OcrDocumentUpdateVO;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static com.consubanco.model.entities.ocr.constant.FailureReason.INVALID_DATE;
import static com.consubanco.model.entities.ocr.constant.FailureReason.UNKNOWN_PERIODICITY;
import static com.consubanco.model.entities.ocr.message.OcrMessage.*;

@UtilityClass
public class PeriodicityValidatorUtil {

    public OcrDocumentUpdateVO validatePeriodicity(OcrDocument ocrDocument, List<OcrDataVO> ocrDataList, OcrDataVO initialPeriod, OcrDataVO finalPeriod) {
        LocalDate initialDate = DateUtil.stringToDate(initialPeriod.getValue());
        LocalDate finalDate = DateUtil.stringToDate(finalPeriod.getValue());
        long daysBetween = ChronoUnit.DAYS.between(initialDate, finalDate);
        if (periodicityIsMonthly(daysBetween)) {
            return monthlyValidation(ocrDocument, ocrDataList, initialDate, finalDate);
        } else if (periodicityIsFortnightly(daysBetween)) {
            return fortnightValidation(ocrDocument, ocrDataList, initialDate, finalDate);
        }
        String reason = unknownPeriodicity(initialDate, finalDate, daysBetween);
        return new OcrDocumentUpdateVO(ocrDocument.getId(), ocrDataList, UNKNOWN_PERIODICITY, reason);
    }

    private static OcrDocumentUpdateVO monthlyValidation(OcrDocument ocrDocument, List<OcrDataVO> ocrDataList, LocalDate initialDate, LocalDate finalDate) {
        LocalDate[] monthlyDates = MonthlyDates.getDatesFromIndex(ocrDocument.getDocumentIndex());
        if (isDateWithinPeriod(monthlyDates, initialDate, finalDate)) {
            return new OcrDocumentUpdateVO(ocrDocument.getId(), ocrDataList);
        }
        String reason = invalidMonthlyPayStub(monthlyDates[0], monthlyDates[1], initialDate, finalDate);
        return new OcrDocumentUpdateVO(ocrDocument.getId(), ocrDataList, INVALID_DATE, reason);
    }

    private static OcrDocumentUpdateVO fortnightValidation(OcrDocument ocrDocument, List<OcrDataVO> ocrDataList, LocalDate initialDate, LocalDate finalDate) {
        int index = ocrDocument.getDocumentIndex();
        LocalDate[] fortnightDates = FortnightDates.getDatesFromIndex(index);
        if (isDateWithinPeriod(fortnightDates, initialDate, finalDate)) {
            return new OcrDocumentUpdateVO(ocrDocument.getId(), ocrDataList);
        }
        String reason = invalidFortnightPayStub(fortnightDates[0], fortnightDates[1], initialDate, finalDate);
        return new OcrDocumentUpdateVO(ocrDocument.getId(), ocrDataList, INVALID_DATE, reason);
    }

    private static boolean periodicityIsMonthly(long daysBetween) {
        return daysBetween >= 28 && daysBetween <= 31;
    }

    private static boolean periodicityIsFortnightly(long daysBetween) {
        return daysBetween >= 14 && daysBetween <= 16;
    }

    private static boolean isDateWithinPeriod(LocalDate[] validDates, LocalDate initialDate, LocalDate finalDate) {
        return (validDates[0].isEqual(initialDate) || validDates[0].isAfter(initialDate)) &&
                (validDates[0].isEqual(finalDate) || validDates[0].isBefore(finalDate));
    }

}
