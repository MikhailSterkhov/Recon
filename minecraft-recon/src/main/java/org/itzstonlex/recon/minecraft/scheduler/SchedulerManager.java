package org.itzstonlex.recon.minecraft.scheduler;

import org.itzstonlex.recon.util.StringRandom;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public final class SchedulerManager {

    private final Map<String, ScheduledFuture<?>> schedulerMap = new HashMap<>();

    private final ScheduledExecutorService scheduledExecutor
            = Executors.newSingleThreadScheduledExecutor();


    /**
     * Запустить асинхронный поток
     *
     * @param command - команда потока
     */
    public void runAsync(Runnable command) {
        scheduledExecutor.submit(command);
    }

    /**
     * Отменить и закрыть поток шедулера
     * по его ID
     *
     * @param schedulerId - ID шедулера
     */
    public void cancelScheduler(String schedulerId) {
        ScheduledFuture<?> scheduledFuture = schedulerMap.get(schedulerId.toLowerCase());

        if ( scheduledFuture == null || scheduledFuture.isCancelled() ) {
            return;
        }

        scheduledFuture.cancel(true);
        schedulerMap.remove(schedulerId.toLowerCase());
    }

    /**
     * Воспроизвести команду Runnable через
     * определенное количество времени
     *
     * @param schedulerId - ID шедулера
     * @param command - команда
     * @param delay - время
     * @param timeUnit - единица времени
     */
    public void runLater(String schedulerId,
                         Runnable command, long delay, TimeUnit timeUnit) {
        ScheduledFuture<?> scheduledFuture = scheduledExecutor.schedule(command, delay, timeUnit);

        schedulerMap.put(schedulerId.toLowerCase(), scheduledFuture);
    }

    public void runLater(Runnable command, long delay, TimeUnit timeUnit) {
        runLater(StringRandom.randomAlphanumeric(32), command, delay, timeUnit);
    }

    /**
     * Воспроизвести команду Runnable через
     * определенное количество времени циклично
     *
     * @param schedulerId - ID шедулера
     * @param command - команда
     * @param delay - время
     * @param period - период цикличного воспроизведения
     * @param timeUnit - единица времени
     */
    public void runTimer(String schedulerId,
                         Runnable command, long delay, long period, TimeUnit timeUnit) {
        ScheduledFuture<?> scheduledFuture
                = scheduledExecutor.scheduleAtFixedRate(command, delay, period, timeUnit);

        schedulerMap.put(schedulerId.toLowerCase(), scheduledFuture);
    }

    public void runTimer(Runnable command, long delay, long period, TimeUnit timeUnit) {
        runTimer(StringRandom.randomAlphanumeric(32), command, delay, period, timeUnit);
    }

    public void cancelAll() {
        new HashSet<>(schedulerMap.keySet()).forEach(this::cancelScheduler);
    }

}
