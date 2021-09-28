package com.emmanuelc.jobmanagementservice.domain;

import com.emmanuelc.jobmanagementservice.domain.enumeration.JobState;

import java.util.concurrent.TimeUnit;

/**
 * Schedule
 * schedules a one-shot task that becomes enabled after a given delay
 */
public final class OneShotScheduler extends Scheduler {
    /**
     * The time (in seconds) from now to delay execution
     */
    private final long delay;

    /**
     * The Job the scheduler is running on
     */
    private Job job;

    OneShotScheduler(final long delay) {
        this.delay = delay;
    }

    @Override
    void startScheduler(final Job job) {
        this.job = job;
        try {
            scheduledExecutor.schedule(this, this.delay, TimeUnit.SECONDS);
        } catch (final Exception exe) {
            this.job.setState(JobState.FAILED);
            scheduledExecutor.shutdownNow();
        }
    }

    @Override
    public void run() {
        try {
            job.execute();
            job.setState(JobState.SUCCESS);
        } catch (Exception e) {
            job.setState(JobState.FAILED);
            scheduledExecutor.shutdownNow();
        }
    }
}
