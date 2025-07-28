package com.example.bankcards.properties;

import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.security.Key;

@ConfigurationProperties(prefix = "jwt")
@Getter
public class JwtProperties {
    private Key secretKey;

    @Setter
    private long expiration;

    public void setSecretKey(String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
    }
}
