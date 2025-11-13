package com.onepiece.paymentservice.config;

import com.onepiece.paymentservice.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(authorize -> authorize

                        // ========== PUBLIC ENDPOINTS (No Auth) ==========
                        .requestMatchers("/actuator/**", "/v3/api-docs/**", "/swagger-ui/**")
                        .permitAll()

                        // ========== ISC ENDPOINTS (Service-to-Service) ==========
                        // ✅ ISC authenticated via X-Service-Secret in JwtAuthenticationFilter
                        // No role-based checks - filter validates service secret
                        .requestMatchers(HttpMethod.POST, "/api/v1/payment-service")
                        .permitAll()  // ✅ CHANGED: Was .hasRole("BUYER"), now .permitAll()

                        // ========== PAYMENT ENDPOINTS WITH ROLE CHECKS ==========

                        // ✅ PUT: Update Payment Status
                        // Only BUYER can update their own payment
                        .requestMatchers(HttpMethod.PUT, "/api/v1/payment-service/{id}")
                        .hasRole("BUYER")

                        // ✅ GET: Get Payments by Seller ID
                        // Only SELLER can view their own payments
                        // Or ADMIN can view any seller's payments
                        .requestMatchers(HttpMethod.GET, "/api/v1/payment-service/seller/{id}")
                        .hasAnyRole("SELLER", "ADMIN")

                        // ✅ GET: Get Payments by Seller ID with Status
                        // Only SELLER can view their own payments with specific status
                        // Or ADMIN can view any seller's payments
                        .requestMatchers(HttpMethod.GET, "/api/v1/payment-service/seller/{id}/{status}")
                        .hasAnyRole("SELLER", "ADMIN")

                        // ✅ GET: Get Payments by Buyer ID
                        // Only BUYER can view their own payments
                        // Or ADMIN can view any buyer's payments
                        .requestMatchers(HttpMethod.GET, "/api/v1/payment-service/buyer/{id}")
                        .hasAnyRole("BUYER", "ADMIN")

                        // ✅ GET: Get Payments by Buyer ID with Status
                        // Only BUYER can view their own payments with specific status
                        // Or ADMIN can view any buyer's payments
                        .requestMatchers(HttpMethod.GET, "/api/v1/payment-service/buyer/{id}/{status}")
                        .hasAnyRole("BUYER", "ADMIN")

                        // ========== DEFAULT: All Other Endpoints ==========
                        // Require authentication (any authenticated user)
                        .anyRequest()
                        .authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}