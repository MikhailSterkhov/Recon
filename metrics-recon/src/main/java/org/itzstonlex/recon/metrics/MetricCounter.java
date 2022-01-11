package org.itzstonlex.recon.metrics;

import java.io.PrintStream;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public final class MetricCounter {

    public static MetricCounter newCounter(String id, String label) {
        return new MetricCounter(id, label);
    }

    public static MetricCounter newCounter(String id) {
        return new MetricCounter(id, "N/A");
    }

    private final String id;
    private final String label;

    private long lastUpdateMillis = System.currentTimeMillis();
    private int value;

    private final Map<MetricTimeSnippet, Integer> snippetsMap;

    private MetricCounter(String id, String label) {
        this.id = id;
        this.label = label;

        // Metric Cache of Time.
        snippetsMap = new HashMap<>();

        // Seconds.
        snippetsMap.put(MetricTimeSnippet.of(1, TimeUnit.SECONDS), 0);
        snippetsMap.put(MetricTimeSnippet.of(5, TimeUnit.SECONDS), 0);
        snippetsMap.put(MetricTimeSnippet.of(10, TimeUnit.SECONDS), 0);
        snippetsMap.put(MetricTimeSnippet.of(15, TimeUnit.SECONDS), 0);
        snippetsMap.put(MetricTimeSnippet.of(30, TimeUnit.SECONDS), 0);

        // Minutes.
        snippetsMap.put(MetricTimeSnippet.of(1, TimeUnit.MINUTES), 0);
        snippetsMap.put(MetricTimeSnippet.of(5, TimeUnit.MINUTES), 0);
        snippetsMap.put(MetricTimeSnippet.of(10, TimeUnit.MINUTES), 0);
        snippetsMap.put(MetricTimeSnippet.of(15, TimeUnit.MINUTES), 0);
        snippetsMap.put(MetricTimeSnippet.of(30, TimeUnit.MINUTES), 0);

        // Hours.
        snippetsMap.put(MetricTimeSnippet.of(1, TimeUnit.HOURS), 0);
        snippetsMap.put(MetricTimeSnippet.of(12, TimeUnit.HOURS), 0);
        snippetsMap.put(MetricTimeSnippet.of(24, TimeUnit.HOURS), 0);
    }

    public Set<MetricTimeSnippet> timeKeys() {
        return snippetsMap.keySet();
    }

    public void printDump(PrintStream printer) {
        assert printer != null;

        snippetsMap.keySet()
                .stream()
                .sorted(Comparator.comparingLong(MetricTimeSnippet::toMillis))

                .forEach(metricTimeSnippet -> printer.printf("[Counter-Dump] %s => %s%n",
                        metricTimeSnippet, snippetsMap.get(metricTimeSnippet)));
    }

    public void addSnippet(MetricTimeSnippet snippet) {
        assert snippet != null;

        // We check for the presence of a key with the
        // same time for caching to extract the possibility
        // of overwriting values.
        for (MetricTimeSnippet current : snippetsMap.keySet()) {

            if (current.toMillis() == snippet.toMillis()) {
                return;
            }
        }

        // Add new time-key.
        snippetsMap.put(snippet, 0);
    }

    public void clearValues() {
        snippetsMap.replaceAll((metricTimeSnippet, integer) -> 0);
    }

    private MetricTimeSnippet optimalKey(long currentMillis) {
        MetricTimeSnippet prev = null;
        MetricTimeSnippet result = null;

        long nearedTime = 1_000;

        for (MetricTimeSnippet metricTimeSnippet : snippetsMap.keySet()) {

            if (metricTimeSnippet.toMillis() == currentMillis) {
                return metricTimeSnippet;
            }

            if (prev != null) {

                if (prev.toMillis() > nearedTime && prev.toMillis() < currentMillis) {
                    nearedTime = prev.toMillis();

                    result = prev;
                }
            }

            prev = metricTimeSnippet;
        }

        return result;
    }

    private void _resetValue(long millisFor, int newValue) {
        for (MetricTimeSnippet current : snippetsMap.keySet()) {

            if (current.toMillis() == millisFor) {
                snippetsMap.put(current, newValue);
            }
        }
    }

    public void set(int newValue) {

        // Update timed values process.
        long currentUpdateMillis = System.currentTimeMillis() - lastUpdateMillis;

        if (currentUpdateMillis > 1_000) {
            MetricTimeSnippet snippet = optimalKey(currentUpdateMillis);

            // If the last update was more than 1 second ago
            if (snippet != null) {

                List<MetricTimeSnippet> sortedMetricKeysList = snippetsMap.keySet()
                        .stream()
                        .sorted(Comparator.comparingLong(MetricTimeSnippet::toMillis))
                        .collect(Collectors.toList());

                int snippetIndex = sortedMetricKeysList.indexOf(snippet);
                int[] cache = sortedMetricKeysList.stream().mapToInt(snippetsMap::get).toArray();

                // Fill values by snippet index as current counter value.
                for (int current = 1; current <= snippetIndex && current < sortedMetricKeysList.size(); current++) {

                    MetricTimeSnippet currentSnippet = sortedMetricKeysList.get(current);
                    _resetValue(currentSnippet.toMillis(), cache[0]);
                }

                // Fill other points from snippet index to cache limit.
                for (int other = 1; snippetIndex + other < sortedMetricKeysList.size(); other++) {

                    MetricTimeSnippet otherSnippet = sortedMetricKeysList.get(snippetIndex + other);
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

    public void divide(int divide) {
        set(value / divide);
    }

    public void multiply(int multiply) {
        set(value * multiply);
    }

    public void pow(int exponent) {
        set((int) Math.pow(value, exponent));
    }

    public void increment() {
        add(1);
    }

    public void decrement() {
        take(1);
    }

    public int valueOf(long timePer, TimeUnit unit) {
        // update cache data.
        set(currentValue());

        // getting process.
        long currentMillis = unit.toMillis(timePer);

        if (currentMillis < 5_000) {
            for (MetricTimeSnippet current : snippetsMap.keySet()) {

                if ((current.getTime() == timePer && current.getUnit() == unit)
                        || (unit == TimeUnit.MILLISECONDS && current.toMillis() == timePer)) {

                    return snippetsMap.get(current);
                }
            }

            return 0;
        }

        MetricTimeSnippet optimalSnippet = optimalKey(currentMillis);
        return optimalSnippet != null ? snippetsMap.get(optimalSnippet) : 0;
    }

    public int valueOf(long millisFor) {
        return valueOf(millisFor, TimeUnit.MILLISECONDS);
    }

    public int valueOf(MetricTimeSnippet snippet) {
        return valueOf(snippet.toMillis());
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
