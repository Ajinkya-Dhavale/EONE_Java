//package com.java.eONE.security;
//
//import io.jsonwebtoken.*;
//import io.jsonwebtoken.security.Keys;
//import jakarta.annotation.PostConstruct;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
//import java.security.Key;
//import java.util.Date;
//
//@Component
//public class JsonWebToken {
//
//    @Value("${app.jwtSecret}")
//    private String jwtSecret;
//
//    @Value("${app.jwtExpirationInMs}")
//    private int jwtExpirationInMs;
//
//    private Key key;
//
//    @PostConstruct
//    public void init() {
//        key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
//    }
//
//    public String encode(Long userId) {
//        Date now = new Date();
//        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);
//
//        return Jwts.builder()
//                .setSubject(Long.toString(userId))
//                .setIssuedAt(now)
//                .setExpiration(expiryDate)
//                .signWith(key, SignatureAlgorithm.HS256)
//                .compact();
//    }
//
//    public boolean validate(String token) {
//        try {
//            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
//            return true;
//        } catch (JwtException ex) {
//            return false;
//        }
//    }
//
//    public Long getUserId(String token) {
//        Claims claims = Jwts.parserBuilder()
//                .setSigningKey(key)
//                .build()
//                .parseClaimsJws(token)
//                .getBody();
//        return Long.parseLong(claims.getSubject());
//    }
//}
