package com.example.leave_management_system.Security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SimpleTokenFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        // If there's no header or it's not our simple token, do nothing.
        if (authHeader == null || !authHeader.startsWith("Bearer SESSION_FLAG_")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extract the role from the token (e.g., "EMPLOYEE" or "ADMIN")
        String token = authHeader.substring(7); // Removes "Bearer "
        String role = token.replace("SESSION_FLAG_", "");

        // Create an authority for this role
        List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(role));

        // Create an authentication object and set it in Spring's security context
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                null, // Principal is null as we don't know the specific user
                null, // Credentials are null
                authorities
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Continue to the next filter in the chain
        filterChain.doFilter(request, response);
    }
}