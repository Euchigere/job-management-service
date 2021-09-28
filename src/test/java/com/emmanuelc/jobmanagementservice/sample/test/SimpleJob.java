package com.emmanuelc.jobmanagementservice.sample.test;

import com.emmanuelc.jobmanagementservice.domain.Job;
import com.emmanuelc.jobmanagementservice.domain.enumeration.Priority;

public class SimpleJob extends Job {
    private volatile static String EXECUTION_RESULTS = "";

    private final long id;

    private final String title;

    public SimpleJob(long id, String title, Priority priority) {
        super(priority);
        this.id = id;
        this.title = title;
    }

    @Override
    public void execute() {
        setExecutionResults(String.format("%sJobId: %d, Title: %s, Priority: %s%n", EXECUTION_RESULTS, id, title, getPriority().toString()));
    }

    public static synchronized void setExecutionResults(String executionResults) {
        EXECUTION_RESULTS = executionResults;
    }



    public static String getExecutionResults() {
        return EXECUTION_RESULTS;
    }

    @Override
    public String toString() {
        return String.format("JobId: %d, Title: %s, Priority: %s%n", id, title, getPriority().toString());
    }
}
