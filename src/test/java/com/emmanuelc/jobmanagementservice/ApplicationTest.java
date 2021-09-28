package com.emmanuelc.jobmanagementservice;


import com.emmanuelc.jobmanagementservice.domain.Scheduler;
import com.emmanuelc.jobmanagementservice.domain.enumeration.JobState;
import com.emmanuelc.jobmanagementservice.domain.enumeration.Priority;
import com.emmanuelc.jobmanagementservice.sample.test.SimpleJob;
import com.emmanuelc.jobmanagementservice.sample.test.SimpleScheduledJob;
import com.emmanuelc.jobmanagementservice.service.JobManagementService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ApplicationTest {
    @AfterEach
    public void reset() {
        SimpleJob.setExecutionResults("");
        SimpleScheduledJob.setExecutionResult("");
    }

    @Test
    public void shouldExecuteJobsBasedOnPriority() throws InterruptedException {
        // The result of this test is likely to fail if the job pool executor is configured to run on more than one thread
        final JobManagementService service = JobManagementService.getInstance();

        final SimpleJob job1 = new SimpleJob(1, "Simple Job 1", Priority.LOW);
        final SimpleJob job2 = new SimpleJob(2, "Simple Job 2", Priority.MEDIUM);
        final SimpleJob job3 = new SimpleJob(3, "Simple Job 3", Priority.HIGH);

        service.addAllJobs(List.of(job1, job2, job3));

        Thread.sleep(500);

        final String expectedExecutionResult = "JobId: 3, Title: Simple Job 3, Priority: HIGH\n"
                + "JobId: 2, Title: Simple Job 2, Priority: MEDIUM\n"
                + "JobId: 1, Title: Simple Job 1, Priority: LOW\n";

        if (Application.NO_OF_J0B_POOL_EXECUTOR_THREADS == 1) {
            assertEquals(expectedExecutionResult, SimpleJob.getExecutionResults());
        }
        assertEquals(JobState.SUCCESS, job1.getState());
        assertEquals(JobState.SUCCESS, job2.getState());
        assertEquals(JobState.SUCCESS, job3.getState());
    }

    @Test
    public void shouldExecuteJobWithoutSideEffectAndMaintainStateConsistency() throws InterruptedException {
        final SimpleJob simpleFailingJob = new SimpleJob(1L, "Simple Failing Job", Priority.LOW) {
            @Override
            public void execute() {
                try {
                    Thread.sleep(400);
                } catch (InterruptedException ignore) {}
                throw new RuntimeException();
            }
        };

        final JobManagementService service = JobManagementService.getInstance();
        service.addJob(simpleFailingJob);

        Thread.sleep(100);

        assertEquals(JobState.RUNNING, simpleFailingJob.getState());

        Thread.sleep(400);

        assertEquals(JobState.FAILED, simpleFailingJob.getState());
    }

    @Test
    public void shouldBeAbleToExecuteJobImmediatelyOrAccordingToASchedule() throws InterruptedException {
        final SimpleJob simpleJob = new SimpleJob(1, "Simple Job", Priority.MEDIUM);

        final Scheduler schedule = Scheduler.newOneShotScheduler(2);
        final SimpleScheduledJob scheduledJob1 = new SimpleScheduledJob(2, "Scheduled Job 1", Priority.HIGH, schedule);

        final Scheduler scheduleAtFixedRate = Scheduler.newFixedRateScheduler(1, LocalDateTime.now().plusSeconds(2));
        final SimpleScheduledJob scheduledJob2 = new SimpleScheduledJob(3, "Scheduled Job 2", Priority.HIGH, scheduleAtFixedRate);

        final JobManagementService service = JobManagementService.getInstance();
        service.addAllJobs(List.of(simpleJob, scheduledJob1, scheduledJob2));

        Thread.sleep(100);

        // The SimpleJob instance is executed immediately
        // Also the instance of the SimpleScheduledJob with FixedRateSchedule instance executes since initial delay is zero
        final String expectedSimpleJobResult = "JobId: 1, Title: Simple Job, Priority: MEDIUM\n";
        String expectedScheduledJobResult = "JobId: 3, Title: Scheduled Job 2, Priority: HIGH\n";

        assertEquals(expectedSimpleJobResult, SimpleJob.getExecutionResults());
        assertEquals(JobState.SUCCESS, simpleJob.getState());

        assertEquals(expectedScheduledJobResult, SimpleScheduledJob.getExecutionResult());
        assertEquals(JobState.RUNNING, scheduledJob1.getState());
        assertEquals(JobState.RUNNING, scheduledJob2.getState());

        Thread.sleep(2000);

        // The instance of the SimpleScheduledJob with FixedRateSchedule instance executes one more time
        // The instance of the SimpleScheduledJob with OneShotSchedule instance is executed after set delay
        expectedScheduledJobResult += "JobId: 3, Title: Scheduled Job 2, Priority: HIGH\n"
                + "JobId: 2, Title: Scheduled Job 1, Priority: HIGH\n";

        assertEquals(expectedScheduledJobResult, SimpleScheduledJob.getExecutionResult());
        assertEquals(JobState.SUCCESS, scheduledJob1.getState());
        assertEquals(JobState.SUCCESS, scheduledJob2.getState());

        assertEquals(expectedSimpleJobResult, SimpleJob.getExecutionResults());
        assertEquals(JobState.SUCCESS, simpleJob.getState());
    }
}
