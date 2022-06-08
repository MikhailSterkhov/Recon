package org.itzstonlex.recon.metrics;

import java.util.*;
import java.util.concurrent.TimeUnit;

public final class MetricCounter {

    public static MetricCounter newCounter(String id, String label) {
        return new MetricCounter(id, label);
    }

    public static MetricCounter newCounter(String id) {
        return new MetricCounter(id, "N/A");
    }

    private final String id;
    private final String label;

    private long lastUpdatedTime;
    private int currently;

    private final Map<Long, Integer> cachedMetricData = new HashMap<>(1024);

    private MetricCounter(String id, String label) {
        this.id = id;
        this.label = label;
    }

    public void reset() {
        cachedMetricData.clear();
    }

    public void set(int newValue) {
        cachedMetricData.put(this.lastUpdatedTime = System.currentTimeMillis(), this.currently = newValue);
    }

    public void add(int add) {
        set(currently + add);
    }

    public void take(int take) {
        set(currently - take);
    }

    public void divide(int divide) {
        set(currently / divide);
    }

    public void multiply(int multiply) {
        set(currently * multiply);
    }

    public void pow(int exponent) {
        set((int) Math.pow(currently, exponent));
    }

    public void increment() {
        add(1);
    }

    public void decrement() {
        take(1);
    }

    public int maxValue() {
        return cachedMetricData.values().stream().max(Comparator.comparingInt(i -> i)).orElse(currentValue());
    }

    public long firstUpdateTimeMillis() {
        return cachedMetricData.keySet().stream().min(Comparator.comparingLong(l -> l)).orElse(0L);
    }

    public long lastUpdateTimeMillis() {
        return lastUpdatedTime;
    }

    public int valueOf(long latestTime, TimeUnit latestUnit) {
        long latestMillis = System.currentTimeMillis() - latestUnit.toMillis(latestTime);
        long firstUpdateMillis = this.firstUpdateTimeMillis();

        if (latestMillis < firstUpdateMillis) {
            return 0;
        }

        if (latestMillis >= lastUpdateTimeMillis()) {
            return currently;
        }

        return this.cachedMetricData.entrySet()
                .stream()

                .filter(entry -> entry.getKey() <= latestMillis)
                .max(Comparator.comparingLong(Map.Entry::getKey))

                .map(Map.Entry::getValue)
                .orElse(0);
    }

    public int valueOf(long latestMillis) {
        return this.valueOf(latestMillis, TimeUnit.MILLISECONDS);
    }

    public String id() {
        return id;
    }

    public String label() {
        return label;
    }

    public int currentValue() {
        return currently;
    }

    @Override
    public String toString() {
        return "@MetricCounter={currentValue=" + currently + "}";
    }

}
