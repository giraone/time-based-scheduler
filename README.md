# Time-based Scheduling in Spring Boot 3 with JobRunr

## JobRunr - Links

- https://www.jobrunr.io/en/documentation/configuration/spring/
- https://www.baeldung.com/java-jobrunr-spring

## Test scenario

- [x] Call [WorldTimeApi](http://worldtimeapi.org/api/timezone/Europe/Berlin) every minute.
- [ ] Call `Thread.sleep(10 + Random.nextInt(20))` every 20 seconds. How does JobRunr behave, when a jobs takes longer?