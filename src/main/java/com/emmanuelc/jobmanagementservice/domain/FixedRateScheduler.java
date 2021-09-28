package com.emmanuelc.jobmanagementservice.domain;

import com.emmanuelc.jobmanagementservice.domain.enumeration.JobState;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * ScheduleAtFixedRate
 * schedules a periodic action that becomes enabled after the given initial delay
 */
public final class FixedRateScheduler extends Scheduler {
    /**
     * Initial delay (in seconds) before periodic action becomes enabled
     */
    private final long initialDelay;

    /**
     * The period (in seconds) between successive executions
     */
    private final long period;

    /**
     * Date and time to cancel scheduler
     * When null, scheduler will continue running
     */
    private final LocalDateTime endDateTime;

    /**
     * The result of scheduling a task with a ScheduledExecutorService.
     */
    private ScheduledFuture<?> scheduledFuture;

    /**
     * The Job the scheduler is running on
     */
    private Job job;

    FixedRateScheduler(final long initialDelay, final long period, final LocalDateTime endDateTime) {
        super();
        this.initialDelay = initialDelay;
        this.period = period;
        this.endDateTime = endDateTime;
    }

    public void startScheduler(final Job job) {
        this.job = job;
        try {
            this.scheduledFuture =
                    scheduledExecutor.scheduleAtFixedRate(this, this.initialDelay, this.period, TimeUnit.SECONDS);
        } catch (final Exception exe) {
            this.job.setState(JobState.FAILED);
            scheduledExecutor.shutdownNow();
        }
    }

    @Override
    public void run() {
        if (shouldCancelScheduler()) {
            job.setState(JobState.SUCCESS);
            scheduledExecutor.shutdownNow();
        } else {
            try {
                job.execute();
            } catch (Exception e) {
                job.setState(JobState.FAILED);
                scheduledExecutor.shutdownNow();
            }
        }
    }

    private boolean shouldCancelScheduler() {
        return this.endDateTime != null
                && ZonedDateTime.now(ZoneId.systemDefault())
                .isAfter(ZonedDateTime.of(this.endDateTime, ZoneId.systemDefault()));
    }
}
