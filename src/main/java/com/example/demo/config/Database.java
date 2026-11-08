package com.example.demo.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class Database {
    private Database() { }

    public static HikariDataSource createDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(env("DB_URL", "jdbc:postgresql://localhost:5432/urlshortener"));
        config.setUsername(env("DB_USER", "app"));
        config.setPassword(env("DB_PASSWORD", "dev_password"));
        config.setMaximumPoolSize(Integer.parseInt(env("DB_POOL_SIZE", "10")));
        config.setPoolName("url-shortener-pool");
        config.setConnectionTimeout(10_000);
        return new HikariDataSource(config);
    }

    public static void initSchema(HikariDataSource dataSource) {
        try (InputStream input = Database.class.getResourceAsStream("/schema.sql")) {
            if (input == null) {
                throw new IllegalStateException("schema.sql was not found in application resources");
            }
            String sql = new String(input.readAllBytes(), StandardCharsets.UTF_8);
            try (Connection connection = dataSource.getConnection(); Statement statement = connection.createStatement()) {
                statement.execute(sql);
            }
        } catch (IOException | SQLException exception) {
            throw new IllegalStateException("Could not initialize database schema", exception);
        }
    }

    private static String env(String key, String defaultValue) {
        String value = System.getenv(key);
        return value == null || value.isBlank() ? defaultValue : value;
    }
}
