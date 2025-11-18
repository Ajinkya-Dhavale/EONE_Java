package com.java.eONE.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class SecurityExceptionHandler implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String errorMessage = "Unauthorized. Please provide a valid authentication token.";
        
        // Check if it's a token-related issue
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            errorMessage = "Missing or invalid Authorization header. Please include a Bearer token.";
        } else {
            errorMessage = "Invalid or expired token. Please login again.";
        }
        
        String jsonResponse = String.format("{\"error\":\"%s\"}", errorMessage);
        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
    }
}

