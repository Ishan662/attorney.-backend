// >> In your existing file: FirebaseTokenFilter.java
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
import org.springframework.security.core.userdetails.UsernameNotFoundException; // Import this exception
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class FirebaseTokenFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;

    // --- ▼▼▼ ADD THIS LIST OF PUBLIC PATHS ▼▼▼ ---
    // A list of paths that should be allowed to bypass this filter's main logic.
    private static final List<String> PUBLIC_PATHS = List.of(
            "/api/auth/register-lawyer",
            "/api/invitations/finalize"
            // You can add more public paths here if needed, e.g., /api/auth/google-sync
    );
    // --- ▲▲▲ ADD THIS LIST OF PUBLIC PATHS ▲▲▲ ---

    @Autowired
    public FirebaseTokenFilter(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        // --- ▼▼▼ ADD THIS NEW CHECK AT THE TOP ▼▼▼ ---
        // Check if the request path is in our list of public paths.
        if (isPublicPath(request)) {
            // If it is a public path, we do NOTHING and just pass the request
            // down the filter chain. This prevents the filter from trying to
            // authenticate a user who is in the process of registering.
            filterChain.doFilter(request, response);
            return;
        }
        // --- ▲▲▲ ADD THIS NEW CHECK AT THE TOP ▲▲▲ ---


        final String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String idToken = header.substring(7);

        try {
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
            String uid = decodedToken.getUid();

            System.out.println("FirebaseTokenFilter: Token verified for UID: " + uid);

            // The user must exist in our DB for any non-public endpoint
            UserDetails userDetails = userDetailsService.loadUserByUsername(uid);

            System.out.println("FirebaseTokenFilter: UserDetails loaded. Authorities: " + userDetails.getAuthorities());

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (FirebaseAuthException | UsernameNotFoundException | IllegalArgumentException e) {
            // If the token is invalid or the user is not found, we clear the context.
            // Spring Security will then deny access because the endpoint is protected.
            // This is correct behavior for protected endpoints.
            System.err.println("Error in FirebaseTokenFilter: " + e.getMessage());
            SecurityContextHolder.clearContext();
            // We can optionally set a 401 Unauthorized status here to be more explicit
            // response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            // return; // uncomment if you add the line above
        }

        filterChain.doFilter(request, response);
    }

    // --- ▼▼▼ ADD THIS NEW HELPER METHOD ▼▼▼ ---
    /**
     * Helper method to check if the current request path is one of the defined public paths.
     * @param request The incoming servlet request.
     * @return true if the path is public, false otherwise.
     */
    private boolean isPublicPath(HttpServletRequest request) {
        String path = request.getServletPath();
        return PUBLIC_PATHS.stream().anyMatch(publicPath -> publicPath.equals(path));
    }
    // --- ▲▲▲ ADD THIS NEW HELPER METHOD ▲▲▲ ---
}