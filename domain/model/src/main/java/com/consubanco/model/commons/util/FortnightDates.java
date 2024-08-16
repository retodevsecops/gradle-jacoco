package com.consubanco.model.commons.util;

import lombok.experimental.UtilityClass;

import java.time.LocalDate;

@UtilityClass
public class FortnightDates {

    private static final int DAY_15 = 15;
    private static final int ONE = 1;

    public static LocalDate[] getDatesFromIndex(int index, int plusDays) {
        LocalDate startDate = calculateStartDate(index);
        LocalDate endDate = calculateEndDate(startDate, plusDays);
        return new LocalDate[]{startDate, endDate};
    }

    private static LocalDate calculateStartDate(int index) {
        LocalDate currentDate = LocalDate.now();
        int minusDays = ((index + ONE) * 15) + 15;
        LocalDate startDate = currentDate.minusDays(minusDays);
        if (startDate.getDayOfMonth() > DAY_15) return startDate.withDayOfMonth(DAY_15);
        return startDate.withDayOfMonth(ONE);
    }

    private static LocalDate calculateEndDate(LocalDate startDate, int plusDays) {
        LocalDate endDate = startDate.plusDays(plusDays);
        if (endDate.getDayOfMonth() > DAY_15) return endDate.withDayOfMonth(endDate.lengthOfMonth());
        return endDate.withDayOfMonth(DAY_15);
    }

}
