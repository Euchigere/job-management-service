package com.emmanuelc.jobmanagementservice.sample;

import com.emmanuelc.jobmanagementservice.domain.Job;
import com.emmanuelc.jobmanagementservice.domain.Scheduler;
import com.emmanuelc.jobmanagementservice.domain.enumeration.Priority;

public class SimpleJob extends Job {
    private final long id;

    private final String title;

    public SimpleJob(long id, String title, Priority priority, Scheduler scheduler) {
        super(priority, scheduler);
        this.id = id;
        this.title = title;
    }

    public SimpleJob(long id, String title, Priority priority) {
        this(id, title, priority, null);
    }

    @Override
    public void execute() {
        System.out.printf("JobId: %d, Title: %s, Priority: %s executed on Thread: %s%n", id, title, getPriority().toString(), Thread.currentThread().getName());
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return String.format("JobId: %d, Title: %s, Priority: %s%n", id, title, getPriority().toString());
    }
}
