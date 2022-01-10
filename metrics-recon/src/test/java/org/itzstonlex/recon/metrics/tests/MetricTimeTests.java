package org.itzstonlex.recon.metrics.tests;

import org.itzstonlex.recon.metrics.MetricCounter;
import org.itzstonlex.recon.metrics.MetricTime;
import org.itzstonlex.recon.metrics.ReconMetrics;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MetricTimeTests {

    public static void main(String[] args) {
        MetricCounter metricCounter = ReconMetrics.TOTAL_BYTES_WRITE;

        metricCounter.addTimeSnippet(MetricTime.of(20, TimeUnit.SECONDS));
        metricCounter.addTimeSnippet(MetricTime.of(40, TimeUnit.SECONDS));
        metricCounter.addTimeSnippet(MetricTime.of(45, TimeUnit.SECONDS));
        metricCounter.addTimeSnippet(MetricTime.of(50, TimeUnit.SECONDS));
        metricCounter.addTimeSnippet(MetricTime.of(55, TimeUnit.SECONDS));

        metricCounter.add(10);
        metricCounter.printDump(System.out);

        runLater(() -> {
            metricCounter.add(50);

            System.out.println("---------------------------------------------------------------");
            metricCounter.printDump(System.out);

        }, 10, TimeUnit.SECONDS);

        runLater(() -> {
            metricCounter.add(50);

            System.out.println("---------------------------------------------------------------");
            metricCounter.printDump(System.out);

        }, 35, TimeUnit.SECONDS);

        runLater(() -> {
            metricCounter.add(100);

            System.out.println("---------------------------------------------------------------");
            metricCounter.printDump(System.out);

        }, 70, TimeUnit.SECONDS);
    }

    private static void runLater(Runnable runnable, long delay, TimeUnit timeUnit) {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.schedule(() -> {

            runnable.run();
            executorService.shutdown();
        }, delay, timeUnit);
    }

}
