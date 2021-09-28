package com.emmanuelc.jobmanagementservice;


import com.emmanuelc.jobmanagementservice.domain.Scheduler;
import com.emmanuelc.jobmanagementservice.domain.enumeration.Priority;
import com.emmanuelc.jobmanagementservice.sample.SimpleJob;
import com.emmanuelc.jobmanagementservice.service.JobManagementService;

import java.time.LocalDateTime;
import java.util.List;

public class Application {
    /**
     *  The no of threads used in executing the job pool.
     * @see com.emmanuelc.jobmanagementservice.service.JobManagementService
     */
    public static final int NO_OF_J0B_POOL_EXECUTOR_THREADS;

    /**
     *  The initial capacity for the JobManagementService's priority blocking queue.
     *  @see com.emmanuelc.jobmanagementservice.service.JobManagementService
     */
    public static final int QUEUE_INITIAL_CAPACITY;

    // initialize static variables from environment variables if set or use default
    static {
        NO_OF_J0B_POOL_EXECUTOR_THREADS = getEnvOrDefault("NO_OF_J0B_POOL_EXECUTOR_THREADS", 1);
        QUEUE_INITIAL_CAPACITY = getEnvOrDefault("QUEUE_INITIAL_CAPACITY", 10);
    }

    private static int getEnvOrDefault(final String name, final int defaultValue) {
        try {
            return Integer.parseInt(System.getenv(name));
        } catch (final NumberFormatException ignore) {}
        return defaultValue;
    }

    public static void main(String[] args) throws InterruptedException {
        // Usage example
        final SimpleJob simpleJob = new SimpleJob(1, "Simple Job", Priority.LOW);
        final SimpleJob simpleJob2 = new SimpleJob(2, "Simple Job 2", Priority.HIGH);

        final Scheduler scheduler = Scheduler.newFixedRateScheduler(0, 2, LocalDateTime.now().plusSeconds(6));
        final SimpleJob scheduledJob = new SimpleJob(3, "Scheduled Job", Priority.MEDIUM, scheduler){
            @Override
            public void execute() {
                System.out.printf(
                        "JobId: %d, Title: %s, Priority: %s executed on Thread: %s at %s%n",
                        this.getId(),
                        this.getTitle(),
                        getPriority().toString(),
                        Thread.currentThread().getName(),
                        LocalDateTime.now()
                );
            }
        };

        final JobManagementService jobManagementService = JobManagementService.getInstance();

        jobManagementService.addAllJobs(List.of(simpleJob, scheduledJob, simpleJob2));

        Thread.sleep(10000);
        System.exit(0);
    }
}
