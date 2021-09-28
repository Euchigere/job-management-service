package com.emmanuelc.jobmanagementservice.service;

import com.emmanuelc.jobmanagementservice.Application;
import com.emmanuelc.jobmanagementservice.domain.Job;

import java.util.Collection;
import java.util.Comparator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * JobManagementService
 * Manages queuing of jobs and prioritizing execution of jobs based on job priority
 */
public class JobManagementService {
    /**
     * A singleton instance of the JobManagementService
     */
    private static JobManagementService INSTANCE;

    /**
     * ExecutorService to execute jobs in queue
     */
    private final ExecutorService jobPoolExecutor;

    /**
     * PriorityBlockingQueue for queuing jobs
     */
    private final PriorityBlockingQueue<Job> priorityBlockingQueue;

    private boolean running = true;

    private JobManagementService() {
        this.jobPoolExecutor  = Executors.newFixedThreadPool(Application.NO_OF_J0B_POOL_EXECUTOR_THREADS);
        this.priorityBlockingQueue = new PriorityBlockingQueue<>(
                Application.QUEUE_INITIAL_CAPACITY,
                Comparator.comparing(Job::getPriority)
        );
        start();
    }

    private void start() {
        final ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            while (running) {
                try {
                    jobPoolExecutor.execute(priorityBlockingQueue.take());
                } catch (InterruptedException ignore) {}
            }
            jobPoolExecutor.shutdown();
        });
        executorService.shutdown();
    }

    public boolean addJob(final Job job) {
        return priorityBlockingQueue.add(job);
    }

    public boolean addAllJobs(final Collection<? extends Job> jobs) {
        return priorityBlockingQueue.addAll(jobs);
    }

    public static JobManagementService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new JobManagementService();
        }
        return INSTANCE;
    }
}
