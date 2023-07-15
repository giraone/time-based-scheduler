package com.giraone.timesched.scheduler.service;

import org.jobrunr.jobs.annotations.Job;
import org.jobrunr.jobs.context.JobContext;
import org.jobrunr.jobs.context.JobDashboardProgressBar;
import org.jobrunr.jobs.context.JobRunrDashboardLogger;
import org.jobrunr.scheduling.JobScheduler;
import org.jobrunr.spring.annotations.Recurring;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A Spring service doing some scheduled jobs.
 */
@Service
public class JobFulfillService {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobFulfillService.class);
    private static final Logger DASHBOARD_LOGGER = new JobRunrDashboardLogger(LOGGER);

    private static final AtomicInteger COUNTER1 = new AtomicInteger();
    private static final AtomicInteger COUNTER2 = new AtomicInteger();
    private static final Random RANDOM = new Random();

    private final WorldTimeApiWebClient worldTimeApiWebClient;
    private final JobScheduler jobScheduler;

    public JobFulfillService(WorldTimeApiWebClient worldTimeApiWebClient, JobScheduler jobScheduler) {
        this.worldTimeApiWebClient = worldTimeApiWebClient;
        this.jobScheduler = jobScheduler;
    }

    @Recurring(id = "fetch-worldtime-job", cron = "* * * * *")
    @Job(name = "Recurring job (every minute) to fetch time from worldtimeapi.org", retries = 1)
    public void doFetchWorldTimeJob() {
        DASHBOARD_LOGGER.info("Fetching time ...");
        Map<String, Object> map = worldTimeApiWebClient.getWorldTime();
        if (map != null) {
            DASHBOARD_LOGGER.info("datetime = {}, unixtime = {}", map.get("datetime"), map.get("unixtime"));
        } else {
            DASHBOARD_LOGGER.error("No time fetched!");
        }
    }

    @Job(name = "Recurring job (every 20 seconds) without overlap (1)")
    public void doNonOverlappingJob1() {
        try {
            int counter = COUNTER1.incrementAndGet();
            long s = System.nanoTime();
            DASHBOARD_LOGGER.info("Starting doNonOverlappingJob1 #{}...", counter);
            Thread.sleep(10 + RANDOM.nextInt(20));
            DASHBOARD_LOGGER.info("Finished doNonOverlappingJob1 #{}!", counter);
            long e =  System.nanoTime();
            long delta = e - s;
            long next = (20 * 1_000_000_000L) - delta;
            jobScheduler.schedule(LocalDateTime.now().plusNanos(next), () -> doNonOverlappingJob1());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Job(name = "Recurring job (every  20 seconds) without overlap (2)")
    public void doNonOverlappingJob2() {
        try {
            int counter = COUNTER2.incrementAndGet();
            DASHBOARD_LOGGER.info("Starting doNonOverlappingJob2 #{}...", counter);
            Thread.sleep(10 + RANDOM.nextInt(20));
            DASHBOARD_LOGGER.info("Finished doNonOverlappingJob2 #{}!", counter);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    //------------------------------------------------------------------------------------------------------------------

    public void doSimpleJob(String anArgument) {
        DASHBOARD_LOGGER.info("Doing some work: anArgument={}", anArgument);
    }

    public void doLongRunningJob(String anArgument) {
        try {
            for (int i = 0; i < 10; i++) {
                DASHBOARD_LOGGER.info("Doing work item {}}: {}", i, anArgument);
                Thread.sleep(20000);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void doLongRunningJobWithJobContext(String anArgument, JobContext jobContext) {
        try {
            DASHBOARD_LOGGER.warn("Starting long running job...");
            final JobDashboardProgressBar progressBar = jobContext.progressBar(10);
            for (int i = 0; i < 10; i++) {
                DASHBOARD_LOGGER.info(String.format("DASHBOARD Processing item %d: %s", i, anArgument));
                LOGGER.info(String.format("LOG Processing item %d: %s", i, anArgument));
                Thread.sleep(15000);
                progressBar.increaseByOne();
            }
            DASHBOARD_LOGGER.warn("Finished long running job...");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
