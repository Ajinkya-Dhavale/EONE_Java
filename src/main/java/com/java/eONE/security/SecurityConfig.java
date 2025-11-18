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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.http.HttpMethod;

//SecurityConfig.java
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

 @Bean
 public SecurityFilterChain securityFilterChain(HttpSecurity http, 
                                                 JwtAuthenticationFilter jwtAuthenticationFilter,
                                                 SecurityExceptionHandler securityExceptionHandler) throws Exception {
     http
         .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
             .requestMatchers("/api/v1/users/register", "/api/v1/auth/login").permitAll()
            // Allow public reads needed pre-login (registration): roles and classrooms
            .requestMatchers(HttpMethod.GET, "/api/v1/roles/**", "/api/v1/classrooms/**").permitAll()
             .requestMatchers("/uploads/**", "/submissionFile/**").permitAll()
             .anyRequest().authenticated()
         )
        .cors(Customizer.withDefaults())
         .sessionManagement(session -> session
             .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
         )
         .exceptionHandling(exceptions -> exceptions
             .authenticationEntryPoint(securityExceptionHandler)
         )
         .httpBasic(Customizer.withDefaults());

     http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

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
