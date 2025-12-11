package com.jobportal.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class DateUtils {

    public static LocalDate startOfMonth() {
        return LocalDate.now().withDayOfMonth(1);
    }

    public static LocalDateTime last30Days() {
        return LocalDateTime.now().minusDays(30);
    }

    public static LocalDateTime now() {
        return LocalDateTime.now();
    }
}
