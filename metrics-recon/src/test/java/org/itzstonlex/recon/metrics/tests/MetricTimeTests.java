package org.itzstonlex.recon.metrics.tests;

import org.itzstonlex.recon.metrics.MetricCounter;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MetricTimeTests {

    public static void main(String[] args) {
        MetricCounter metricCounter = MetricCounter.newCounter("Test");
        System.out.println("Metric Counter (" + metricCounter.id() + " | " + metricCounter.label() + ") is started!");

        metricCounter.add(10);
        System.out.println(metricCounter.currentValue());
        System.out.println(metricCounter.valueOf(1, TimeUnit.SECONDS));
        System.out.println(metricCounter.valueOf(5, TimeUnit.SECONDS));

        runLater(() -> {
            metricCounter.add(50);

            System.out.println("---------------------------------------------------------------");
            System.out.println(metricCounter.currentValue());
            System.out.println(metricCounter.valueOf(1, TimeUnit.SECONDS));
            System.out.println(metricCounter.valueOf(5, TimeUnit.SECONDS));

        }, 10, TimeUnit.SECONDS);

        runLater(() -> {
            metricCounter.add(50);

            System.out.println("---------------------------------------------------------------");
            System.out.println(metricCounter.currentValue());
            System.out.println(metricCounter.valueOf(1, TimeUnit.SECONDS));
            System.out.println(metricCounter.valueOf(5, TimeUnit.SECONDS));
            System.out.println(metricCounter.valueOf(10, TimeUnit.SECONDS));
            System.out.println(metricCounter.valueOf(20, TimeUnit.SECONDS));
            System.out.println(metricCounter.valueOf(30, TimeUnit.SECONDS));
            System.out.println(metricCounter.valueOf(1, TimeUnit.MINUTES));

        }, 35, TimeUnit.SECONDS);

        runLater(() -> {
            metricCounter.add(100);

            System.out.println("---------------------------------------------------------------");
            System.out.println(metricCounter.valueOf(1, TimeUnit.SECONDS));
            System.out.println(metricCounter.valueOf(5, TimeUnit.SECONDS));
            System.out.println(metricCounter.valueOf(10, TimeUnit.SECONDS));
            System.out.println(metricCounter.valueOf(20, TimeUnit.SECONDS));
            System.out.println(metricCounter.valueOf(30, TimeUnit.SECONDS));
            System.out.println(metricCounter.valueOf(1, TimeUnit.MINUTES));
            System.out.println(metricCounter.valueOf(90, TimeUnit.SECONDS));
            System.out.println(metricCounter.valueOf(2, TimeUnit.MINUTES));

        }, 70, TimeUnit.SECONDS);
    }

    private static final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private static void runLater(Runnable runnable, long delay, TimeUnit timeUnit) {
        executorService.schedule(() -> {

            runnable.run();
            executorService.shutdown();
        }, delay, timeUnit);
    }

}
