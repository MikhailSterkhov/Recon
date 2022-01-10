package org.itzstonlex.recon.metrics;

import java.io.PrintStream;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public final class MetricCounter {

    private final String id;
    private final String label;

    private long lastUpdateMillis = System.currentTimeMillis();
    private int value;

    private final Map<MetricTime, Integer> timeSnippetsMap;

    MetricCounter(String id, String label) {
        this.id = id;
        this.label = label;

        // Metric Cache of Time.
        timeSnippetsMap = new HashMap<>();

        // Seconds.
        timeSnippetsMap.put(MetricTime.of(1, TimeUnit.SECONDS), 0);
        timeSnippetsMap.put(MetricTime.of(5, TimeUnit.SECONDS), 0);
        timeSnippetsMap.put(MetricTime.of(10, TimeUnit.SECONDS), 0);
        timeSnippetsMap.put(MetricTime.of(15, TimeUnit.SECONDS), 0);
        timeSnippetsMap.put(MetricTime.of(30, TimeUnit.SECONDS), 0);

        // Minutes.
        timeSnippetsMap.put(MetricTime.of(1, TimeUnit.MINUTES), 0);
        timeSnippetsMap.put(MetricTime.of(5, TimeUnit.MINUTES), 0);
        timeSnippetsMap.put(MetricTime.of(10, TimeUnit.MINUTES), 0);
        timeSnippetsMap.put(MetricTime.of(15, TimeUnit.MINUTES), 0);
        timeSnippetsMap.put(MetricTime.of(30, TimeUnit.MINUTES), 0);

        // Hours.
        timeSnippetsMap.put(MetricTime.of(1, TimeUnit.HOURS), 0);
        timeSnippetsMap.put(MetricTime.of(12, TimeUnit.HOURS), 0);
        timeSnippetsMap.put(MetricTime.of(24, TimeUnit.HOURS), 0);
    }

    public Set<MetricTime> timeKeys() {
        return timeSnippetsMap.keySet();
    }

    public void printDump(PrintStream printer) {
        assert printer != null;

        timeSnippetsMap.keySet()
                .stream()
                .sorted(Comparator.comparingLong(MetricTime::toMillis))

                .forEach(metricTime -> printer.printf("[MetricCounter] %s => %s%n",
                        metricTime, timeSnippetsMap.get(metricTime)));
    }

    public void addTimeSnippet(MetricTime metricTime) {
        assert metricTime != null;

        // We check for the presence of a key with the
        // same time for caching to extract the possibility
        // of overwriting values.
        for (MetricTime current : timeSnippetsMap.keySet()) {

            if (current.toMillis() == metricTime.toMillis()) {
                return;
            }
        }

        // Add new time-key.
        timeSnippetsMap.put(metricTime, 0);
    }

    private MetricTime optimalKey(long currentMillis) {
        MetricTime prev = null;
        MetricTime result = null;

        long nearedTime = 1_000;

        for (MetricTime metricTime : timeSnippetsMap.keySet()) {

            if (metricTime.toMillis() == currentMillis) {
                return metricTime;
            }

            if (prev != null) {

                if (prev.toMillis() > nearedTime && prev.toMillis() < currentMillis) {
                    nearedTime = prev.toMillis();

                    result = prev;
                }
            }

            prev = metricTime;
        }

        return result;
    }

    private void _resetValue(long millisFor, int newValue) {
        for (MetricTime current : timeSnippetsMap.keySet()) {

            if (current.toMillis() == millisFor) {
                timeSnippetsMap.put(current, newValue);
            }
        }
    }

    public void set(int newValue) {

        // Update timed values process.
        long currentUpdateMillis = System.currentTimeMillis() - lastUpdateMillis;

        if (currentUpdateMillis > 1_000) {
            MetricTime snippet = optimalKey(currentUpdateMillis);

            // If the last update was more than 1 second ago
            if (snippet != null) {

                List<MetricTime> sortedMetricKeysList = timeSnippetsMap.keySet()
                        .stream()
                        .sorted(Comparator.comparingLong(MetricTime::toMillis))
                        .collect(Collectors.toList());

                int snippetIndex = sortedMetricKeysList.indexOf(snippet);
                int[] cache = sortedMetricKeysList.stream().mapToInt(timeSnippetsMap::get).toArray();

                // Fill values by snippet index as current counter value.
                for (int current = 1; current <= snippetIndex && current < sortedMetricKeysList.size(); current++) {

                    MetricTime currentSnippet = sortedMetricKeysList.get(current);
                    _resetValue(currentSnippet.toMillis(), cache[0]);
                }

                // Fill other points from snippet index to cache limit.
                for (int other = 1; snippetIndex + other < sortedMetricKeysList.size(); other++) {

                    MetricTime otherSnippet = sortedMetricKeysList.get(snippetIndex + other);
                    _resetValue(otherSnippet.toMillis(), cache[other]);
                }

                // Refresh time of the last update.
                lastUpdateMillis = System.currentTimeMillis();
            }
        }

        this.value = newValue;
        _resetValue(1_000, newValue);
    }

    public void add(int add) {
        set(value + add);
    }

    public void take(int take) {
        set(value - take);
    }

    public void increment() {
        add(1);
    }

    public void decrement() {
        take(1);
    }

    public int valueOf(long timeFor, TimeUnit unit) {
        // update cache data.
        set(currentValue());

        // getting process.
        long currentMillis = unit.toMillis(timeFor);

        if (currentMillis < 5_000) {
            for (MetricTime current : timeSnippetsMap.keySet()) {

                if ((current.getTime() == timeFor && current.getUnit() == unit)
                        || (unit == TimeUnit.MILLISECONDS && current.toMillis() == timeFor)) {

                    return timeSnippetsMap.get(current);
                }
            }

            return 0;
        }

        MetricTime optimalKey = optimalKey(currentMillis);
        return optimalKey != null ? timeSnippetsMap.get(optimalKey) : 0;
    }

    public int valueOf(long millisFor) {
        return valueOf(millisFor, TimeUnit.MILLISECONDS);
    }

    public int valueOf(MetricTime metricTime) {
        return valueOf(metricTime.toMillis());
    }

    public String id() {
        return id;
    }

    public String label() {
        return label;
    }

    public int currentValue() {
        return value;
    }

    @Override
    public String toString() {
        return "@MetricCounter={currentValue=" + value + "}";
    }

}
