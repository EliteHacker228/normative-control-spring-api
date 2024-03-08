package ru.maeasoftoworks.normativecontrol.api.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;

public class DateUtils {
    public static Date localDateTimeToDate(LocalDateTime localDateTime){
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static String dateToIsoFormattedString(Date date){
        return new Date().toInstant().atOffset(ZoneOffset.of("+05:00")).toString();
    }
}
