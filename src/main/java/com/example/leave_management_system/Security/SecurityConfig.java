package com.example.leave_management_system.Security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final SimpleTokenFilter simpleTokenFilter; // Inject our new filter

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // 1. Allow anyone to access the login endpoint
                        .requestMatchers("/login/**").permitAll()

                        // 2. Rule: Only users with 'ADMIN' authority can see all leaves
                        .requestMatchers(HttpMethod.GET, "/api/leaves").hasAuthority("ADMIN")

                        // 3. Rule: Any authenticated user (ADMIN or EMPLOYEE) can access other leave endpoints
                        .requestMatchers("/api/leaves/**").authenticated()

                        // 4. Rule: All other requests not specified above must be authenticated
                        .anyRequest().authenticated()
                )
                // We use stateless sessions; the token contains all needed info
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Add our custom filter to the security chain
                .addFilterBefore(simpleTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}