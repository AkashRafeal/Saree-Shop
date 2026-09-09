package com.sareeshop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SareeShopApplication {

    public static void main(String[] args) {
        SpringApplication.run(SareeShopApplication.class, args);
    }
}
