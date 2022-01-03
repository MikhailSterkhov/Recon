package org.itzstonlex.recon.log;

import org.itzstonlex.recon.util.TimeUtils;

import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class ConnectionLogger extends Logger {

    public static final String LOG_FORMAT = "[{time}][{name} {level}]: {message}";

    public ConnectionLogger(String name) {
        super(name, null);
    }

    @Override
    public void log(LogRecord record) {
        String currentTime = TimeUtils.format(TimeUtils.CLOCKED_TIME_PATTERN);
        String level = record.getLevel().getName();

        String message = LOG_FORMAT.replace("{time}", currentTime)
                .replace("{name}", getName())
                .replace("{level}", level)
                .replace("{message}", record.getMessage());

        System.out.println(message);
    }

}
