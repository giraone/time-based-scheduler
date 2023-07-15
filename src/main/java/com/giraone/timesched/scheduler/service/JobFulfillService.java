package com.giraone.timesched.scheduler.service;

import org.jobrunr.jobs.annotations.Job;
import org.jobrunr.jobs.context.JobContext;
import org.jobrunr.jobs.context.JobDashboardProgressBar;
import org.jobrunr.jobs.context.JobRunrDashboardLogger;
import org.jobrunr.spring.annotations.Recurring;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * A Spring service doing some scheduled jobs.
 */
@Service
public class JobFulfillService {

    private static final Logger LOGGER =LoggerFactory.getLogger(JobFulfillService.class);
    private static final Logger DASHBOARD_LOGGER = new JobRunrDashboardLogger(LOGGER);

    private final WorldTimeApiWebClient worldTimeApiWebClient;

    public JobFulfillService(WorldTimeApiWebClient worldTimeApiWebClient) {
        this.worldTimeApiWebClient = worldTimeApiWebClient;
    }

    @Recurring(id = "fetch-worldtime-job", cron = "* * * * *")
    @Job(name = "Recurring job (every minute) to fetch time from worldtimeapi.org")
    public void doFetchWorldTimeJob() {
        DASHBOARD_LOGGER.info("Fetching time ...");
        Map<String, Object> map = worldTimeApiWebClient.getWorldTime();
        if (map != null) {
            DASHBOARD_LOGGER.info("datetime = {}, unixtime = {}", map.get("datetime"), map.get("unixtime"));
        } else {
            DASHBOARD_LOGGER.error("No time fetched!");
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
