package com.sareeaura.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.net.URI;

@Configuration
public class DatabaseConfig {

    private static final Logger log = LoggerFactory.getLogger(DatabaseConfig.class);

    @Bean
    @Primary
    public DataSource dataSource(
            @Value("${spring.datasource.url}") String defaultUrl,
            @Value("${spring.datasource.username:}") String defaultUsername,
            @Value("${spring.datasource.password:}") String defaultPassword
    ) {
        HikariConfig config = new HikariConfig();
        String databaseUrlEnv = System.getenv("DATABASE_URL");

        if (databaseUrlEnv != null && !databaseUrlEnv.isBlank() && 
            (databaseUrlEnv.startsWith("postgres://") || databaseUrlEnv.startsWith("postgresql://"))) {
            try {
                log.info("Render DATABASE_URL detected. Converting to PostgreSQL JDBC configuration.");
                String cleanUrl = databaseUrlEnv.replaceFirst("^postgres(ql)?://", "http://");
                URI uri = new URI(cleanUrl);

                String host = uri.getHost();
                int port = uri.getPort() == -1 ? 5432 : uri.getPort();
                String path = uri.getPath();
                String userInfo = uri.getUserInfo();

                String dbUser = defaultUsername;
                String dbPass = defaultPassword;

                if (userInfo != null && userInfo.contains(":")) {
                    String[] parts = userInfo.split(":", 2);
                    dbUser = parts[0];
                    dbPass = parts[1];
                }

                String jdbcUrl = "jdbc:postgresql://" + host + ":" + port + path;
                config.setJdbcUrl(jdbcUrl);
                config.setUsername(dbUser);
                config.setPassword(dbPass);
                config.setDriverClassName("org.postgresql.Driver");
                log.info("Configured PostgreSQL DataSource for host: {}, database: {}", host, path);
            } catch (Exception e) {
                log.error("Failed to parse DATABASE_URL, falling back to default configuration: {}", e.getMessage());
                config.setJdbcUrl(defaultUrl);
                config.setUsername(defaultUsername);
                config.setPassword(defaultPassword);
            }
        } else {
            config.setJdbcUrl(defaultUrl);
            config.setUsername(defaultUsername);
            config.setPassword(defaultPassword);
        }

        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setIdleTimeout(30000);
        config.setConnectionTimeout(30000);
        config.setMaxLifetime(1800000);

        return new HikariDataSource(config);
    }
}
