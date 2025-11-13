package com.onepiece.bidding_service.config;

import com.onepiece.bidding_service.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Spring Security Configuration
 *
 * Security Architecture:
 * 1. JwtAuthenticationFilter - Reads headers from API Gateway
 * 2. RBAC - Role-based access control on endpoints
 * 3. CORS - Allow frontend requests
 * 4. Exception Handling - Custom 401 and 403 responses
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        // ✅ Disable CSRF for stateless API
        http.csrf(AbstractHttpConfigurer::disable);

        // ✅ Add JWT filter before authentication filter
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        // ✅ Configure authorization with role-based access control
        http.authorizeHttpRequests(request -> request
                // PUBLIC ENDPOINTS (GET requests only)
                .requestMatchers("GET", "/api/v1/bidding-service/auctions").authenticated()
                .requestMatchers("GET", "/api/v1/bidding-service/auction/**").authenticated()
                .requestMatchers("GET", "/api/v1/bidding-service/auctions/product/**").authenticated()
                .requestMatchers("GET", "/api/v1/bidding-service/auctions/status/**").authenticated()
                .requestMatchers("GET", "/api/v1/bidding-service/auctions/seller/**").authenticated()
                .requestMatchers("GET", "/api/v1/bidding-service/bids").authenticated()
                .requestMatchers("GET", "/api/v1/bidding-service/bid/**").authenticated()
                .requestMatchers("GET", "/api/v1/bidding-service/bids/auction/**").authenticated()
                .requestMatchers("GET", "/api/v1/bidding-service/bids/buyer/**").authenticated()

                // Actuator endpoints
                .requestMatchers("/actuator/**").permitAll()
                .requestMatchers("/v3/api-docs/**").permitAll()
                .requestMatchers("/swagger-ui/**").permitAll()

                // PROTECTED ENDPOINTS - Require authentication
                .requestMatchers("PUT", "/api/v1/bidding-service/auction/**").authenticated()
                .requestMatchers("DELETE", "/api/v1/bidding-service/auction/**").authenticated()
                .requestMatchers("POST", "/api/v1/bidding-service/bids/**").authenticated()

                // Admin only endpoints (example)
                .requestMatchers("DELETE", "/api/v1/bidding-service/**").hasRole("ADMIN")
                .requestMatchers("DELETE", "/api/v1/bidding-service/bid/**").hasRole("ADMIN")

                .requestMatchers("POST", "/api/v1/bidding-service/auctions/**").permitAll()
                // All other requests require authentication
                .anyRequest().authenticated()
        );
        http.exceptionHandling(exception -> exception
                .authenticationEntryPoint(customAuthenticationEntryPoint)
                .accessDeniedHandler(customAccessDeniedHandler));

        return http.build();
    }

}