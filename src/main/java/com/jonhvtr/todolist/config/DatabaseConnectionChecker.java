package com.jonhvtr.todolist.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Slf4j
@Component
public class DatabaseConnectionChecker {
    private final DataSource dataSource;

    public DatabaseConnectionChecker(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @PostConstruct
    public void checkConnection() {
        try(Connection conn = dataSource.getConnection()) {
            log.info("Database connection established successfully: {}", conn.getMetaData().getURL());
        } catch (SQLException e) {
            log.error("Failed to establish database connection: {}", e.getMessage());
        }
    }
}
