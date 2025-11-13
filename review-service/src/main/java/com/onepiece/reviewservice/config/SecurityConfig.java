package com.onepiece.reviewservice.config;

import com.onepiece.reviewservice.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
                        // ========== REVIEW SERVICE RULES ==========

                        // ✅ GET: Everyone can view reviews
                        .requestMatchers(HttpMethod.GET, "/api/v1/review-service/**")
                        .authenticated()

                        // ✅ POST: Only BUYER and SELLER can create (NOT ADMIN)
                        .requestMatchers(HttpMethod.POST, "/api/v1/review-service/create")
                        .hasAnyRole("BUYER", "SELLER")

                        // ✅ PUT: Only BUYER and SELLER can update (NOT ADMIN)
                        .requestMatchers(HttpMethod.PUT, "/api/v1/review-service/**")
                        .hasAnyRole("BUYER", "SELLER")

                        // ✅ DELETE: Only BUYER and SELLER can delete (NOT ADMIN)
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/review-service/**")
                        .hasAnyRole("BUYER", "SELLER")

                        // ========== OTHER ENDPOINTS ==========

                        // Public docs
                        .requestMatchers("/actuator/**", "/v3/api-docs/**", "/swagger-ui/**")
                        .permitAll()

                        // Everything else requires authentication
                        .anyRequest()
                        .authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}