package com.example.backend.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class FirebaseTokenFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;

    @Autowired
    public FirebaseTokenFilter(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        final String header = request.getHeader("Authorization");

        // If there's no token, we just pass the request along.
        // If the endpoint is protected, Spring Security's authorization filter
        // will deny access later. If it's public, it will be allowed.
        // This is the key change.
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String idToken = header.substring(7);

        try {
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
            String uid = decodedToken.getUid();

            // If token is valid, try to load the user and set authentication context.
            UserDetails userDetails = userDetailsService.loadUserByUsername(uid);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (FirebaseAuthException | IllegalArgumentException e) {
            // If token is invalid, we can log it but we DON'T send an error.
            // We just clear the context and let the request proceed.
            // Spring's authorization will then handle it. If the endpoint is protected,
            // it will fail with a 401. If it's public, it will succeed.
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}