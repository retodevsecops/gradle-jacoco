package com.consubanco.model.commons.util;

import lombok.experimental.UtilityClass;

import java.time.LocalDate;

@UtilityClass
public class MonthlyDates {

    private static final int DAY_1 = 1;
    private static final int DAY_20 = 20;
    private static final int ZERO = 0;
    private static final int ONE = 1;

    public static LocalDate[] getDatesFromIndex(int index) {
        if (index == ZERO) return getCurrentMonthly();
        LocalDate currentDate = LocalDate.now();
        if (currentDate.getDayOfMonth() <= DAY_20) {
            LocalDate startDate = currentDate.minusMonths(index + ONE).withDayOfMonth(DAY_1);
            LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
            return new LocalDate[]{startDate, endDate};
        }
        LocalDate startDate = currentDate.minusMonths(index + ONE).withDayOfMonth(DAY_1);
        LocalDate endDateMonth = currentDate.minusMonths(index);
        LocalDate endDate = endDateMonth.withDayOfMonth(endDateMonth.lengthOfMonth());
        return new LocalDate[]{startDate, endDate};
    }

    private static LocalDate[] getCurrentMonthly() {
        LocalDate currentDate = LocalDate.now();
        if (currentDate.getDayOfMonth() <= DAY_20) {
            LocalDate startDate = currentDate.minusMonths(ONE).withDayOfMonth(DAY_1);
            LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
            return new LocalDate[]{startDate, endDate};
        }
        LocalDate startDate = currentDate.minusMonths(ONE).withDayOfMonth(DAY_1);
        LocalDate endDate = currentDate.withDayOfMonth(currentDate.lengthOfMonth());
        return new LocalDate[]{startDate, endDate};
    }

}
