package com.example.backend.config;

// ▼▼▼ ADD THIS MISSING IMPORT STATEMENT ▼▼▼
import com.example.backend.config.FirebaseTokenFilter;
// ▲▲▲ ADD THIS MISSING IMPORT STATEMENT ▲▲▲

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    // Now the compiler knows what 'FirebaseTokenFilter' is because of the import
    private final FirebaseTokenFilter firebaseTokenFilter;

    // Using constructor injection (best practice)
    @Autowired
    public SecurityConfig(FirebaseTokenFilter firebaseTokenFilter) {
        this.firebaseTokenFilter = firebaseTokenFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // Disable legacy security features not needed for stateless APIs
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())

                // Ensure our API is stateless
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Define authorization rules for our endpoints
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers(
                                "/api/auth/register-lawyer",
                                "/api/invitations/finalize",
                                "/api/invitations/details"
                        ).permitAll()
                        .requestMatchers(
                                "/api/auth/status",
                                "/api/auth/activate-account",
                                "/api/auth/google-sync",
                                "/api/otp/send",
                                "/api/otp/verify"
                        ).authenticated()
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().denyAll()
                )

                // Add our custom Firebase filter into the Spring Security filter chain
                .addFilterBefore(firebaseTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // This is the origin of your React frontend
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        // Allow all common HTTP methods
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // Allow specific headers, including Authorization for your token
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With"));
        // This is important for the browser to read the response
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Apply this configuration to all paths in your application
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}