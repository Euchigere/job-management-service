package com.emmanuelc.jobmanagementservice.sample.test;

import com.emmanuelc.jobmanagementservice.domain.Job;
import com.emmanuelc.jobmanagementservice.domain.Scheduler;
import com.emmanuelc.jobmanagementservice.domain.enumeration.Priority;

public class SimpleScheduledJob extends Job {
    private volatile static String EXECUTION_RESULT = "";

    private long id;

    private String title;

    public SimpleScheduledJob(long id, String title, Priority priority, Scheduler scheduler) {
        super(priority, scheduler);
        this.id = id;
        this.title = title;
    }

    @Override
    public void execute() {
        setExecutionResult(String.format("%sJobId: %d, Title: %s, Priority: %s%n", EXECUTION_RESULT,id , title, getPriority().toString()));
    }

    public static synchronized void setExecutionResult(String executionResult) {
        EXECUTION_RESULT = executionResult;
    }

    public static String getExecutionResult() {
        return EXECUTION_RESULT;
    }

    @Override
    public String toString() {
        return String.format("JobId: %d, Title: %s, Priority: %s%n", id, title, getPriority().toString());
    }
}

