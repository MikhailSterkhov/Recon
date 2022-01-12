package org.itzstonlex.recon.ui.scheduler;

import org.apache.commons.lang3.RandomStringUtils;
import org.itzstonlex.recon.ui.ReconUILauncher;

import java.util.concurrent.TimeUnit;

public abstract class TaskScheduler implements Runnable {

    private final String identifier;

    public TaskScheduler(String id) {
        this.identifier = id;
    }

    public TaskScheduler() {
        this(RandomStringUtils.randomAlphanumeric(32));
    }


    /**
     * Отмена и закрытие потока
     */
    public void cancel() {
        ReconUILauncher.getInstance().getSchedulerManager().cancelScheduler(identifier);
    }

    /**
     * Запустить асинхронный поток
     */
    public void runAsync() {
        ReconUILauncher.getInstance().getSchedulerManager().runAsync(this);
    }

    /**
     * Запустить поток через определенное
     * количество времени
     *
     * @param delay - время
     * @param timeUnit - единица времени
     */
    public void runLater(long delay, TimeUnit timeUnit) {
        ReconUILauncher.getInstance().getSchedulerManager().runLater(identifier, this, delay, timeUnit);
    }

    /**
     * Запустить цикличный поток через
     * определенное количество времени
     *
     * @param delay - время
     * @param period - период цикличного воспроизведения
     * @param timeUnit - единица времени
     */
    public void runTimer(long delay, long period, TimeUnit timeUnit) {
        ReconUILauncher.getInstance().getSchedulerManager().runTimer(identifier, this, delay, period, timeUnit);
    }

}
