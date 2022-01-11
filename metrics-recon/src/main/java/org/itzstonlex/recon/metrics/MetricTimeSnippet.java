package org.itzstonlex.recon.metrics;

import java.util.concurrent.TimeUnit;

public final class MetricTimeSnippet {

    public static MetricTimeSnippet of(long time, TimeUnit unit) {
        return new MetricTimeSnippet(time, unit);
    }

    private final long value;
    private final TimeUnit unit;

    private final String stringLine;

    private MetricTimeSnippet(long value, TimeUnit unit) {
        this.value = value;
        this.unit = unit;

        this.stringLine = String.format("@MetricTime={time=%s, unit=%s}", value, unit);
    }

    public long getTime() {
        return value;
    }

    public TimeUnit getUnit() {
        return unit;
    }

    public long toMillis() {
        return unit.toMillis(value);
    }

    @Override
    public String toString() {
        return stringLine;
    }
}
