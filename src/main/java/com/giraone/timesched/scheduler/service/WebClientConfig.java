package com.giraone.timesched.scheduler.service;

import io.micrometer.observation.ObservationRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

/**
 * Configuration for WebClient stuff.
 */
@Configuration
public class WebClientConfig {
    
    @Bean
    ObservationRegistry observationRegistry() {
        return ObservationRegistry.create();
    }

    @Bean
    WebClient webClient(ObservationRegistry observationRegistry) {
        String host = "worldtimeapi.org";
        int port = 80;
        return WebClient.builder()
            .baseUrl("http://" + host + ":" + port)
            .observationRegistry(observationRegistry)
            .build();
    }

    @Bean
    WorldTimeApiWebClient worldTimeApiWebClient(WebClient webClient) {
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builder()
            .clientAdapter(WebClientAdapter.forClient(webClient))
            .build();
        return factory.createClient(WorldTimeApiWebClient.class);
    }
}
