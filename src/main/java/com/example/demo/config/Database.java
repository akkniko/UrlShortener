package com.example.demo.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Statement;

public final class Database {
    private Database() {}

    public static DataSource createDataSource() {
        HikariConfig cfg = new HikariConfig();
        cfg.setJdbcUrl(env("DB_URL", "jdbc:postgresql://localhost:5432/urlshortener"));
        cfg.setUsername(env("DB_USER", "app"));
        cfg.setPassword(env("DB_PASSWORD", "dev_password"));
        cfg.setMaximumPoolSize(10);
        return new HikariDataSource(cfg);
    }

    public static void initSchema(DataSource ds) throws Exception {
        try (var in = Database.class.getResourceAsStream("/schema.sql")) {
            String sql = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            try (Connection c = ds.getConnection(); Statement st = c.createStatement()) {
                st.execute(sql);
            }
        }
    }

    private static String env(String key, String def) {
        String v = System.getenv(key);
        return v != null ? v : def;
    }
}
