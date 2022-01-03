package org.itzstonlex.recon.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public final class TimeUtils {

    public static final String CLOCKED_TIME_PATTERN     = "HH:mm:ss";
    public static final String SEQUENCE_TIME_PATTERN    = "HH'h' mm'm' ss's'";
    public static final String DATE_PATTERN             = "EEE dd.MM.yyy";

    public static String format(String pattern, long millis) {
        return new SimpleDateFormat(pattern).format(new Date(millis));
    }

    public static String format(String pattern) {
        return format(pattern, System.currentTimeMillis());
    }

}
