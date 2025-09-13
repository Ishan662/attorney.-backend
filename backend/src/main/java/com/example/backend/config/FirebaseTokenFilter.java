// >> In your existing file: config/FirebaseTokenFilter.java
package com.example.backend.config;

import com.example.backend.model.UserStatus;
import com.example.backend.repositories.UserRepository; // Import UserRepository
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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
public class FirebaseTokenFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;

    @Autowired
    public FirebaseTokenFilter(UserDetailsService userDetailsService, UserRepository userRepository) {
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository; // We need this for the optimized status check
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        final String header = request.getHeader("Authorization");

        // 1. If no token, pass through. Spring Security will handle public/protected endpoints.
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String idToken = header.substring(7);
        FirebaseToken decodedToken;

        try {
            decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
        } catch (FirebaseAuthException e) {
            // Token is invalid (expired, bad signature, etc.)
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid Firebase Token");
            return;
        }

        String uid = decodedToken.getUid();

        // 2. Check if the user is already fully authenticated in this request's context
        if (SecurityContextHolder.getContext().getAuthentication() == null) {

            try {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(uid);

                // If the line above succeeds, it means the user's status is ACTIVE.
                // We can create a full authentication object with all their roles/authorities.
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (UsernameNotFoundException e) {
                // This exception is thrown by our UserDetailsService if the user status is NOT 'ACTIVE'.
                // This is our signal that the user is in a "partially authenticated" state.
                // In this state, they have proven their identity (valid token) but are not yet fully privileged.

                // We create a "lightweight" authentication object. It proves they are authenticated
                // but contains NO roles or special authorities.
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        uid, null, List.of()); // An empty list of authorities
                SecurityContextHolder.getContext().setAuthentication(authentication);

                System.out.println("User is authenticated but not active. UID: " + uid + ". Status likely pending.");
            }

        }

        // 3. Continue the filter chain.
        // Now, Spring Security's AuthorizationFilter will run AFTER us. It will check the
        // rules in SecurityConfig against the Authentication object we just created.
        filterChain.doFilter(request, response);
    }
}