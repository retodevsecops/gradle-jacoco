package com.consubanco.model.commons.util;

import lombok.experimental.UtilityClass;

import java.time.LocalDate;

@UtilityClass
public class FortnightDates {

    private static final int DAY_1 = 1;
    private static final int DAY_14 = 14;
    private static final int DAY_15 = 15;
    private static final int DAY_16 = 16;
    private static final int ZERO = 0;
    private static final int ONE = 1;
    private static final int TWO = 2;

    public static LocalDate[] getDatesFromIndex(int index) {
        if (index == ZERO) return getCurrentFortnight();
        return (index % TWO == ZERO) ? getDatesFirstFortnight(index) : getDatesSecondFortnight(index);
    }

    private static LocalDate[] getCurrentFortnight() {
        LocalDate currentDate = LocalDate.now();
        if (currentDate.getDayOfMonth() >= DAY_15) {
            LocalDate startDate = currentDate.withDayOfMonth(DAY_1);
            LocalDate endDate = currentDate.withDayOfMonth(currentDate.lengthOfMonth());
            return new LocalDate[]{startDate, endDate};
        }
        LocalDate startDate = currentDate.minusMonths(ONE).withDayOfMonth(DAY_15);
        LocalDate endDate = currentDate.withDayOfMonth(DAY_15);
        return new LocalDate[]{startDate, endDate};
    }

    private static LocalDate[] getDatesFirstFortnight(int index) {
        LocalDate currentDate = LocalDate.now();
        int minusMonths = index / TWO;
        if (currentDate.getDayOfMonth() <= DAY_15) {
            LocalDate startDate = currentDate.minusMonths(minusMonths).withDayOfMonth(DAY_1);
            LocalDate endDate = startDate.plusDays(DAY_14);
            return new LocalDate[]{startDate, endDate};
        }
        LocalDate startDate = currentDate.minusMonths(minusMonths).withDayOfMonth(DAY_1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        return new LocalDate[]{startDate, endDate};
    }

    private static LocalDate[] getDatesSecondFortnight(int index) {
        LocalDate currentDate = LocalDate.now();
        int minusMonths = (index + ONE) / TWO;
        if (currentDate.getDayOfMonth() <= DAY_15) {
            LocalDate startDate = currentDate.minusMonths(minusMonths).withDayOfMonth(DAY_1);
            LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
            return new LocalDate[]{startDate, endDate};
        }
        LocalDate startDate = currentDate.minusMonths(minusMonths).withDayOfMonth(DAY_16);
        LocalDate endDateWithoutDay = currentDate.minusMonths(index / TWO);
        LocalDate endDate = endDateWithoutDay.withDayOfMonth(endDateWithoutDay.lengthOfMonth());
        return new LocalDate[]{startDate, endDate};
    }

}
