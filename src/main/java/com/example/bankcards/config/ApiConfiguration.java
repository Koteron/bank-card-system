package com.example.bankcards.config;

import com.example.bankcards.properties.CurrencyProperties;
import com.example.bankcards.properties.JwtProperties;
import com.example.bankcards.properties.SecurityProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
        JwtProperties.class,
        SecurityProperties.class,
        CurrencyProperties.class})
public class ApiConfiguration {
}
