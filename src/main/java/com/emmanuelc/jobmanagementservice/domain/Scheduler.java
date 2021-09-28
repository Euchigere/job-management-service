package com.emmanuelc.jobmanagementservice.domain;

import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Scheduler
 */
public abstract class Scheduler implements Runnable {
    /**
     * The ScheduledExecutorService for scheduling task
     */
    final ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor();

    /**
     * Method to start scheduler
     * @param job the job instance the scheduler will run on
     */
    abstract void startScheduler(final Job job);

    public static Scheduler newOneShotScheduler(final long delay) {
        return new OneShotScheduler(delay);
    }

    public static Scheduler newFixedRateScheduler(
            final long initialDelay,
            final long period,
            final LocalDateTime endDateTime
    ) {
        return new FixedRateScheduler(initialDelay, period, endDateTime);
    }

    public static Scheduler newFixedRateScheduler(final long period, final LocalDateTime endDateTime) {
        return newFixedRateScheduler(0, period, endDateTime);
    }

    public static Scheduler newFixedRateScheduler(final long initialDelay, final long period) {
        return newFixedRateScheduler(initialDelay, period, null);
    }

    public static Scheduler newFixedRateScheduler(final long period) {
        return newFixedRateScheduler(0, period, null);
    }
}
