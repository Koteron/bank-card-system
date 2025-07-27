package com.example.bankcards.security;

import com.example.bankcards.properties.JwtProperties;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    private final JwtProperties jwtProperties;

    public String generateToken(String email) {
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtProperties.getExpiration()))
                .signWith(jwtProperties.getSecretKey())
                .compact();
    }

    public boolean validateToken(String token) {
        Jwts.parser().verifyWith((SecretKey) jwtProperties.getSecretKey()).build().parse(token);
        return true;
    }

    public String extractEmail(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) jwtProperties.getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
}
