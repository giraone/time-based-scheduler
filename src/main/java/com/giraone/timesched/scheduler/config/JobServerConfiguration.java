package com.giraone.timesched.scheduler.config;

import org.h2.jdbcx.JdbcDataSource;
import org.h2.tools.Server;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.SQLException;

@Configuration
public class JobServerConfiguration {

    @ConditionalOnProperty(prefix = "spring.datasource", name = "mode", havingValue = "postgres")
    @Bean
    public DataSource postgresDataSource() {
//        DriverManagerDataSource driver = new DriverManagerDataSource();
//        driver.setDriverClassName("org.postgresql.Driver");
//        driver.setUrl("jdbc:postgresql://localhost:5432/jobrunr");
//        driver.setUsername("user");
//        driver.setPassword("password");
//        return driver;
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName("org.postgresql.Driver");
        dataSourceBuilder.url("jdbc:postgresql://localhost:5432/jobrunr");
        dataSourceBuilder.username("user");
        dataSourceBuilder.password("password");
        return dataSourceBuilder.build();
    }

    @Bean
    @DependsOn("inMemoryH2DatabaseServer")
    public DataSource h2DataSource() {
        final JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setUrl("jdbc:h2:tcp://localhost:9090/mem:jobrunr-db;DB_CLOSE_DELAY=-1");
        return dataSource;
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public Server inMemoryH2DatabaseServer() throws SQLException {
        return Server.createTcpServer("-tcp", "-ifNotExists", "-tcpAllowOthers", "-tcpPort", "9090");
    }
}
