package org.itzstonlex.recon.minecraft.scheduler;

import org.itzstonlex.recon.util.StringRandom;

import java.util.concurrent.TimeUnit;

public abstract class TaskScheduler implements Runnable {

    private final String identifier;

    public TaskScheduler(String id) {
        this.identifier = id;
    }

    public TaskScheduler() {
        this(StringRandom.randomAlphanumeric(32));
    }

    /**
     * Отмена и закрытие потока
     */
    public void cancel(SchedulerManager schedulerManager) {
        schedulerManager.cancelScheduler(identifier);
    }

    /**
     * Запустить асинхронный поток
     */
    public void runAsync(SchedulerManager schedulerManager) {
        schedulerManager.runAsync(this);
    }

    /**
     * Запустить поток через определенное
     * количество времени
     *
     * @param delay - время
     * @param timeUnit - единица времени
     */
    public void runLater(SchedulerManager schedulerManager, long delay, TimeUnit timeUnit) {
        schedulerManager.runLater(identifier, this, delay, timeUnit);
    }

    /**
     * Запустить цикличный поток через
     * определенное количество времени
     *
     * @param delay - время
     * @param period - период цикличного воспроизведения
     * @param timeUnit - единица времени
     */
    public void runTimer(SchedulerManager schedulerManager, long delay, long period, TimeUnit timeUnit) {
        schedulerManager.runTimer(identifier, this, delay, period, timeUnit);
    }

}
