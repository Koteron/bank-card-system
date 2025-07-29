package com.example.bankcards;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@SpringBootApplication
@EnableWebSecurity
public class BankControlApplication {
    public static void main(String[] args) {
        SpringApplication.run(BankControlApplication.class, args);
    }
}
