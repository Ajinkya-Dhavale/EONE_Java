package com.java.eONE.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {

    private final Key key;

    // Inject secret key from application.properties
    public JwtUtil(@Value("${jwt.secret}") String secret) {
        // Use HS256 key from secret string bytes
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    // Generate token with expiration (default 24h)
    public String generateToken(Map<String, Object> claims, long expirationMillis) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMillis);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // Overload for 24h default expiration
    public String generateToken(Map<String, Object> claims) {
        return generateToken(claims, 24 * 60 * 60 * 1000);
    }

    // Validate & parse claims
    public Claims validateAndGetClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException ex) {
            // log or handle exceptions (expired, malformed, etc.)
            return null;
        }
    }
}
