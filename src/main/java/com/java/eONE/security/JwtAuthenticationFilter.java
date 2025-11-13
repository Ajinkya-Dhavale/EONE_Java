package com.java.eONE.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Skip JWT validation for public endpoints
        String path = request.getRequestURI();
        if (path.startsWith("/api/v1/auth/login") || 
            path.startsWith("/api/v1/users/register") ||
            path.startsWith("/uploads/") ||
            path.startsWith("/submissionFile/") ||
            request.getMethod().equals("OPTIONS")) {
            filterChain.doFilter(request, response);
            return;
        }

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                Claims claims = jwtUtil.validateAndGetClaims(token);
                if (claims != null) {
                    Object userId = claims.get("id");
                    Object role = claims.get("role");

                    List<GrantedAuthority> authorities = new ArrayList<>();
                    if (role != null) {
                        // Spring convention expects ROLE_ prefix for simple role checks
                        authorities.add(new SimpleGrantedAuthority("ROLE_" + role.toString()));
                    }

                    Authentication auth = new UsernamePasswordAuthenticationToken(
                            Objects.toString(userId, null),
                            null,
                            authorities
                    );
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    System.out.println("JWT authentication successful for user: " + userId + ", role: " + role + ", path: " + path);
                } else {
                    // Token is invalid or expired
                    System.out.println("JWT token validation failed for request: " + request.getMethod() + " " + path);
                    System.out.println("Token provided: " + (token.length() > 20 ? token.substring(0, 20) + "..." : token));
                }
            } catch (Exception e) {
                System.out.println("JWT token processing error for " + request.getMethod() + " " + path + ": " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("No Authorization header found for request: " + request.getMethod() + " " + path);
        }

        filterChain.doFilter(request, response);
    }
}


