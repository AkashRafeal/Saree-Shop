package com.sareeaura;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SareeAuraApplication {

    public static void main(String[] args) {
        SpringApplication.run(SareeAuraApplication.class, args);
    }
}
