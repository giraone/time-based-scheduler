package com.giraone.timesched.scheduler;

import com.giraone.timesched.scheduler.config.ApplicationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;

@SpringBootApplication
public class SchedulerApplication {

    private final static Logger LOGGER = LoggerFactory.getLogger(SchedulerApplication.class);

    private final ApplicationProperties applicationProperties;

    public SchedulerApplication(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(SchedulerApplication.class);
        Environment env = app.run(args).getEnvironment();
        logApplicationStartup(env);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void startup() {
        LOGGER.info("SchedulerApplication READY");
        if (applicationProperties.isShowConfigOnStartup()) {
            LOGGER.info("{}", applicationProperties);
        }
    }

    private static void logApplicationStartup(Environment env) {

        String protocol = "http";
        if (env.getProperty("server.ssl.key-store") != null) {
            protocol = "https";
        }
        String serverPort = env.getProperty("server.port");
        String contextPath = env.getProperty("server.servlet.context-path");
        if (!StringUtils.hasText(contextPath)) {
            contextPath = "/";
        }
        String hostAddress = "localhost";
        try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            LOGGER.warn("The host name could not be determined, using `localhost` as fallback");
        }
        LOGGER.info("""
                ----------------------------------------------------------
                ~~~ Application '{}' is running! Access URLs:
                ~~~ - Local:      {}://localhost:{}{}
                ~~~ - External:   {}://{}:{}{}
                ~~~ Java version:      {} / {}
                ~~~ Processors:        {}
                ~~~ Profile(s):        {}
                ~~~ Default charset:   {}
                ~~~ File encoding:     {}
                ----------------------------------------------------------""",
            env.getProperty("spring.application.name"),
            protocol,
            serverPort,
            contextPath,
            protocol,
            hostAddress,
            serverPort,
            contextPath,
            System.getProperty("java.version"), System.getProperty("java.vm.name"),
            Runtime.getRuntime().availableProcessors(),
            env.getActiveProfiles(),
            Charset.defaultCharset().displayName(),
            System.getProperty("file.encoding")
        );
    }
}
