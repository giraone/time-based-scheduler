package com.giraone.timesched.scheduler.controller;

import com.giraone.timesched.scheduler.service.JobFulfillService;
import com.giraone.timesched.scheduler.service.WorldTimeApiWebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * A REST controller simply for testing code.
 */
@RestController
@RequestMapping("/manual")
public class ManualJobExecuteController {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobFulfillService.class);

    private final WorldTimeApiWebClient worldTimeApiWebClient;

    public ManualJobExecuteController(WorldTimeApiWebClient worldTimeApiWebClient) {
        this.worldTimeApiWebClient = worldTimeApiWebClient;
    }

    @GetMapping(value = "/worldtimeapi", produces = {MediaType.TEXT_PLAIN_VALUE})
    public String worldtimeapi() {
        LOGGER.info("Fetching time ...");
        Map<String, Object> map = worldTimeApiWebClient.getWorldTime();
        if (map != null) {
            LOGGER.info("datetime = {}, unixtime = {}", map.get("datetime"), map.get("unixtime"));
            return (String) map.get("datetime");
        } else {
            LOGGER.error("No time fetched!");
            return "No time fetched!";
        }
    }
}
