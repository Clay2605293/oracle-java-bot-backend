package com.oraclejavabot.features.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

    private final String SECRET = "oracle-java-bot-secret-key-oracle-java-bot-secret-key";

    private final SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes());

    public String generateToken(UUID userId, String email, Integer rolId) {

        return Jwts.builder()
                .setSubject(email)
                .claim("userId", userId.toString())
                .claim("rolId", rolId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(key)
                .compact();
    }
}