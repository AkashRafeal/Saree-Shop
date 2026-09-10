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

        // 1. Check all possible URL sources
        String rawUrl = System.getenv("DATABASE_URL");
        if (rawUrl == null || rawUrl.isBlank()) {
            rawUrl = System.getenv("SPRING_DATASOURCE_URL");
        }
        if (rawUrl == null || rawUrl.isBlank()) {
            rawUrl = defaultUrl;
        }

        log.info("Resolving DataSource. Raw URL pattern: {}", sanitizeUrl(rawUrl));

        String finalJdbcUrl = rawUrl;
        String finalUsername = defaultUsername;
        String finalPassword = defaultPassword;

        // Handle raw postgres:// or postgresql:// from Render/Railway/Heroku
        if (rawUrl != null && (rawUrl.startsWith("postgres://") || rawUrl.startsWith("postgresql://"))) {
            try {
                String cleanUrl = rawUrl.replaceFirst("^postgres(ql)?://", "http://");
                URI uri = new URI(cleanUrl);

                String host = uri.getHost();
                int port = uri.getPort() == -1 ? 5432 : uri.getPort();
                String path = uri.getPath();
                if (path != null && !path.startsWith("/")) {
                    path = "/" + path;
                }

                String userInfo = uri.getUserInfo();
                if (userInfo != null && userInfo.contains(":")) {
                    String[] parts = userInfo.split(":", 2);
                    finalUsername = parts[0];
                    finalPassword = parts[1];
                }

                finalJdbcUrl = "jdbc:postgresql://" + host + ":" + port + (path != null ? path : "/sareeaura_db");
                config.setDriverClassName("org.postgresql.Driver");
                log.info("Successfully converted to PostgreSQL JDBC URL: {}:{}", host, port);
            } catch (Exception e) {
                log.error("Failed to parse URI, falling back to prefixing jdbc: {}", e.getMessage());
                finalJdbcUrl = "jdbc:" + rawUrl;
                config.setDriverClassName("org.postgresql.Driver");
            }
        } else if (rawUrl != null && rawUrl.startsWith("jdbc:postgresql:")) {
            finalJdbcUrl = rawUrl;
            config.setDriverClassName("org.postgresql.Driver");
        } else if (rawUrl != null && rawUrl.startsWith("jdbc:mysql:")) {
            finalJdbcUrl = rawUrl;
            config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        }

        config.setJdbcUrl(finalJdbcUrl);
        if (finalUsername != null && !finalUsername.isBlank()) {
            config.setUsername(finalUsername);
        }
        if (finalPassword != null && !finalPassword.isBlank()) {
            config.setPassword(finalPassword);
        }

        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setIdleTimeout(30000);
        config.setConnectionTimeout(30000);
        config.setMaxLifetime(1800000);

        return new HikariDataSource(config);
    }

    private String sanitizeUrl(String url) {
        if (url == null) return "null";
        return url.replaceAll(":[^:@/]+@", ":***@");
    }
}
