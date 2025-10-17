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

        if (authHeader == null || !authHeader.startsWith("Bearer SESSION_FLAG_")) {
            filterChain.doFilter(request, response);
            return;
        }

        // --- THIS LOGIC IS NEW ---
        try {
            String token = authHeader.substring(7); // Removes "Bearer "
            String[] parts = token.replace("SESSION_FLAG_", "").split("_", 2);

            if (parts.length != 2) {
                filterChain.doFilter(request, response);
                return;
            }

            String role = parts[0];
            String username = parts[1];

            // Now the Authentication object knows WHO the user is (the principal)
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    username, // Principal is the username
                    null,
                    Collections.singletonList(new SimpleGrantedAuthority(role))
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (Exception e) {
            // If token parsing fails, clear the context
            SecurityContextHolder.clearContext();
        }
        // --- END OF NEW LOGIC ---

        filterChain.doFilter(request, response);
    }
}