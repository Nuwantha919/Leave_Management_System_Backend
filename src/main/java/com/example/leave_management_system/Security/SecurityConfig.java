package com.example.leave_management_system.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration for the REST API.
 * Sets the application as stateless and configures public/protected endpoints.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Disable CSRF (Cross-Site Request Forgery) protection since we are using token-based authentication (JWT)
                .csrf(AbstractHttpConfigurer::disable)

                // 2. Set session management to stateless (essential for REST APIs/JWT)
                // Spring Security will not create or use HTTP sessions
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 3. Define authorization rules for endpoints
                .authorizeHttpRequests(auth -> auth
                        // Allow public access to the /login endpoint
                        .requestMatchers("/login").permitAll()

                        // All other requests must be authenticated (will be handled by JWT later)
                        .anyRequest().authenticated()
                );

        // This line is often needed to correctly build the chain in newer Spring Security versions
        return http.build();
    }

    // NOTE: We don't need to define an AuthenticationManager or UserDetailsService yet,
    // as we are manually handling the authentication in AuthController/AuthService for the first step.
}
