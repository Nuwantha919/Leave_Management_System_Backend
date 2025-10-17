package com.example.leave_management_system.Security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // <-- IMPORT THIS
import org.springframework.security.crypto.password.PasswordEncoder; // <-- IMPORT THIS
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final SimpleTokenFilter simpleTokenFilter;

    // --- THIS IS THE REQUIRED ADDITION ---
    /**
     * Creates a BCryptPasswordEncoder bean.
     * This is the standard, secure way to handle password hashing.
     * @return The password encoder instance.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    // --- END OF ADDITION ---

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/leaves").hasAuthority("ADMIN")
                        .requestMatchers("/api/leaves/**").authenticated()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(simpleTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}