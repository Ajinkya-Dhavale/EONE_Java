package com.java.eONE.security;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.http.SessionCreationPolicy;

//SecurityConfig.java
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

 @Bean
 public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
     http
         .csrf(csrf -> csrf.disable()) // Disable CSRF for APIs
         .authorizeHttpRequests(auth -> auth
             // Allow public access to login and register endpoints
             .requestMatchers("/api/v1/users/register", "/api/v1/auth/login").permitAll()
             // All other endpoints require authentication (can relax if you want open APIs)
             .anyRequest().permitAll()  // <-- temporarily allow all for easier testing
         )
         .sessionManagement(session -> session
             .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // No sessions, stateless
         )
         .httpBasic(Customizer.withDefaults());  // Optional: enable basic auth if needed

     return http.build();
 }

 @Bean
 public PasswordEncoder passwordEncoder() {
     return new BCryptPasswordEncoder();
 }

 @Bean
 public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
     return config.getAuthenticationManager();
 }
}
