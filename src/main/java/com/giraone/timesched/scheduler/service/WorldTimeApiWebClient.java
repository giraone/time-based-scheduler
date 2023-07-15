package com.giraone.timesched.scheduler.service;

import org.springframework.web.service.annotation.GetExchange;

import java.util.Map;

public interface WorldTimeApiWebClient {

    @GetExchange("/api/timezone/Europe/Berlin")
    Map<String,Object> getWorldTime();
}
