package com.emmanuelc.jobmanagementservice.domain;

import com.emmanuelc.jobmanagementservice.domain.enumeration.JobState;
import com.emmanuelc.jobmanagementservice.domain.enumeration.Priority;

/**
 * Job
 */
public abstract class Job implements Runnable {
    /**
     * Possible job state
     */
    private JobState state;

    /**
     * Priority of job
     * Defaults to the lowest priority when not explicitly set
     */
    private Priority priority;

    /**
     * Handles scheduling of job execution
     * When not set, Job is executed immediately
     */
    private Scheduler scheduler;

    private Job(final JobState state, final Priority priority, final Scheduler scheduler) {
        this.state = state;
        this.priority = priority;
        this.scheduler = scheduler;
    }

    public Job(final Priority priority, final Scheduler scheduler) {
        this(JobState.QUEUED, priority, scheduler);
    }

    public Job(final Priority priority) {
        this(JobState.QUEUED, priority, null);
    }

    public Job(final Scheduler scheduler) {
        this(JobState.QUEUED, Priority.LOW, scheduler);
    }

    public Job() {
        this(JobState.QUEUED, Priority.LOW, null);
    }

    /**
     * Method to run task for each Job instance.
     * To be implemented by Job concrete subclasses
     */
    public abstract void execute();

    @Override
    public final void run() {
        this.setState(JobState.RUNNING);
        try {
            if (this.scheduler == null) {
                this.execute();
                this.setState(JobState.SUCCESS);
            } else {
                scheduler.startScheduler(this);
            }
        } catch (final Exception exe) {
            this.setState(JobState.FAILED);
        }
    }

    public JobState getState() {
        return state;
    }

    final void setState(final JobState state) {
        this.state = state;
    }

    public Priority getPriority() {
        return priority;
    }

    public final void setPriority(final Priority priority) {
        if (isLocked()) return;
        this.priority = priority;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public final void setScheduler(final Scheduler scheduler) {
        if (isLocked()) return;
        this.scheduler = scheduler;
    }

    public final boolean isLocked() {
        return state.equals(JobState.RUNNING);
    }
}
