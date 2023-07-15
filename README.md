# Time-based Scheduling in Spring Boot 3 with JobRunr

## JobRunr - Links

- https://www.jobrunr.io/en/documentation/configuration/spring/
- https://www.baeldung.com/java-jobrunr-spring

## Test scenario

- [x] Call [WorldTimeApi](http://worldtimeapi.org/api/timezone/Europe/Berlin) every minute.
- [ ] Call `Thread.sleep(30 + Random.nextInt(60))` every minute. How does JobRunr behave, when a jobs takes longer?