package ru.maeasoftoworks.normativecontrol.api;

import org.junit.jupiter.api.Test;
import ru.maeasoftoworks.normativecontrol.api.utils.DateUtils;

import java.time.ZoneOffset;
import java.util.Date;

public class DateTests {
    @Test
    public void dateTest() {
        System.out.println(DateUtils.dateToIsoFormattedString(new Date()));
    }
}
