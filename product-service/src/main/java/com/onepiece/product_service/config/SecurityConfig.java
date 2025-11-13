package com.onepiece.product_service.config;

import com.onepiece.product_service.filter.JwtAuthenticationFilter;
import lombok.extern.slf4j.Slf4j;
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
@EnableMethodSecurity
@Slf4j
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.info("========== SECURITY FILTER CHAIN CONFIGURATION ==========");

        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/actuator/**",
                                "/v3/api-docs/**",
                                "/swagger-ui/**"
                        ).permitAll()

                        .requestMatchers(HttpMethod.POST, "/api/v1/product-service/products/mark-sold")
                        .permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/v1/product-service/products/**")
                        .permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/v1/product-service/product/**")
                        .permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/v1/product-service/product-images/**")
                        .permitAll()

                        .requestMatchers(HttpMethod.POST, "/api/v1/product-service/products/add-product")
                        .hasRole("SELLER")

                        .requestMatchers(HttpMethod.PUT, "/api/v1/product-service/products/update/**")
                        .hasRole("SELLER")

                        .requestMatchers(HttpMethod.DELETE, "/api/v1/product-service/products/delete/**")
                        .hasRole("SELLER")

                        .requestMatchers(HttpMethod.POST, "/api/v1/product-service/product-images/add/**")
                        .hasRole("SELLER")

                        .requestMatchers(HttpMethod.POST, "/api/v1/product-service/product-images/add-multiple/**")
                        .hasRole("SELLER")

                        .requestMatchers(HttpMethod.PUT, "/api/v1/product-service/product-images/**")
                        .hasRole("SELLER")

                        .requestMatchers(HttpMethod.DELETE, "/api/v1/product-service/product-images/**")
                        .hasRole("SELLER")

                        .requestMatchers(HttpMethod.GET, "/api/v1/product-service/admin/products/**")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.PUT, "/api/v1/product-service/admin/products/**")
                        .hasRole("ADMIN")

                        .anyRequest()
                        .authenticated()
                );

        log.info("========== SECURITY FILTER CHAIN CONFIGURATION COMPLETE ==========");
        return http.build();
    }
}