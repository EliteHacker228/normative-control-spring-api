package ru.maeasoftoworks.normativecontrol.api.utils;

import java.util.Random;

public class CorrelationIdUtils {
    private static final Random random = new Random();
    private static final long LOWER_BOUND = 1000000000000000L;
    private static final long UPPER_BOUND = 10000000000000000L;
    public static String generateCorrelationId(){
        return String.valueOf(random.nextLong(UPPER_BOUND - LOWER_BOUND) + LOWER_BOUND);
    }
}
