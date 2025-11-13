package org.genc.usermgmt.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.genc.usermgmt.dto.CustomUserDetails;
import org.genc.usermgmt.service.impl.CustomUserDetailsService;
import org.genc.usermgmt.util.JwtUtil;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    private static final List<String> SKIPPED_PATHS = List.of(
            "/api/v1/userservice/login",
            "/api/v1/userservice/register",
            "/actuator",
            "/v3/api-docs",
            "/swagger-ui"
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();

        if (request.getMethod().equals(HttpMethod.POST.name())) {
            if (path.startsWith("/api/v1/userservice/login") || path.startsWith("/api/v1/userservice/register")) {
                return true;
            }
        }

        return SKIPPED_PATHS.stream().anyMatch(path::startsWith);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestPath = request.getRequestURI();
        log.info("=== JWT Filter Processing: {} ===", requestPath);

        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        // Step 1: Extract token from header
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            log.info("✓ Authorization header found, token extracted");

            try {
                username = jwtUtil.extractUsername(token);
                log.info("Username extracted from token: {}", username);
            } catch (Exception e) {
                log.error("Error extracting username from token: {}", e.getMessage(), e);
            }
        } else {
            log.warn("No Authorization header or invalid format");
        }

        // Step 2: Validate and authenticate
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            log.info("Step 2: Loading user details for username: {}", username);

            try {
                CustomUserDetails userDetails = userDetailsService.loadUserByUsername(username);
                log.info("UserDetails loaded. Username: {}, Authorities: {}",
                        userDetails.getUsername(), userDetails.getAuthorities());

                // Step 3: Validate token
                log.info("Step 3: Validating token...");

                // ✅ FIXED: Changed signature - validateToken now only takes token parameter
                boolean isTokenValid = jwtUtil.validateToken(token);

                if (isTokenValid) {
                    log.info("Token is valid");

                    // Extract authorities from JWT
                    Collection<? extends GrantedAuthority> authorities = extractAuthoritiesFromToken(token);
                    log.info("✓ Authorities extracted from token: {}", authorities);

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.info("✓✓✓ AUTHENTICATION SUCCESSFUL - User {} authenticated with authorities: {}",
                            username, authorities);
                } else {
                    log.error("✗ Token validation failed for user: {}", username);
                }
            } catch (Exception e) {
                log.error("✗ Exception during authentication: {}", e.getMessage(), e);
            }
        } else {
            if (username == null) {
                log.debug("⚠ Username is null - token extraction may have failed");
            }
            if (SecurityContextHolder.getContext().getAuthentication() != null) {
                log.debug("⚠ Authentication already exists in context");
            }
        }

        filterChain.doFilter(request, response);
    }

    private Collection<? extends GrantedAuthority> extractAuthoritiesFromToken(String token) {
        try {
            String rolesString = jwtUtil.extractRolesFromToken(token);
            log.info("Roles string from token: '{}'", rolesString);

            if (rolesString == null || rolesString.isEmpty()) {
                log.warn("⚠ No roles found in JWT token");
                return List.of();
            }

            Collection<? extends GrantedAuthority> authorities = Arrays.stream(rolesString.split(","))
                    .map(String::trim)
                    .map(role -> {
                        log.debug("  - Adding authority: {}", role);
                        return new SimpleGrantedAuthority(role);
                    })
                    .collect(Collectors.toList());

            log.info("✓ Extracted {} authorities: {}", authorities.size(), authorities);
            return authorities;
        } catch (Exception e) {
            log.error("✗ Error extracting authorities from token: {}", e.getMessage(), e);
            return List.of();
        }
    }
}