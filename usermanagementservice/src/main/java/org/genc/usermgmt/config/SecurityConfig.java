package org.genc.usermgmt.config;


import lombok.RequiredArgsConstructor;
import org.genc.usermgmt.filter.JwtAuthenticationFilter;
import org.genc.usermgmt.security.CustomAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod; // Added import
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity; // Added import
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer; // Added import
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List; // Corrected import to List

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Added for method-level security (e.g., @PreAuthorize)
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;

    private static  final String BASE_SERVICE_PATH= "/api/v1/userservice";

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // 1. Enable CORS using the bean defined below
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 2. Configure Exception Handling to use our custom entry point for 401s
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint)
                )

                // 3. Disable CSRF for REST APIs
                .csrf(AbstractHttpConfigurer::disable)

                // 4. Set session management to stateless (Crucial for JWT)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 5. Define access rules for API endpoints
                .authorizeHttpRequests(authorize -> authorize
                        // Public endpoints for authentication
                        .requestMatchers(HttpMethod.POST, BASE_SERVICE_PATH + "/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, BASE_SERVICE_PATH + "/auth/register").permitAll()

                        // Public API documentation endpoints
                        .requestMatchers("/actuator/**", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()

                        // --- AUCTION PROJECT SPECIFIC RULES ---
                        // Only ADMIN can manage roles
                        .requestMatchers(BASE_SERVICE_PATH + "/roles/**").hasRole("ADMIN")

                        // Only ADMIN can view all users
                        .requestMatchers(HttpMethod.GET, BASE_SERVICE_PATH + "/users").hasRole("ADMIN")

                        // Any authenticated user can access their own profile
                        .requestMatchers(BASE_SERVICE_PATH + "/users/**").authenticated()

                        // All other endpoints require authentication
                        .anyRequest().authenticated()
                )

                // 6. Add our custom JWT filter BEFORE Spring's default authentication filter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);


        return http.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Use List.of for immutable, cleaner lists
        configuration.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:5173"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
