package com.onepiece.product_service.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final List<String> SKIPPED_PATHS = List.of(
            "/actuator",
            "/v3/api-docs",
            "/swagger-ui"
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        boolean skip = SKIPPED_PATHS.stream().anyMatch(path::startsWith);
        if (skip) {
            log.debug("Skipping filter for path: {}", path);
        }
        return skip;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        log.info("========== JWT FILTER STARTED ==========");
        log.info("URI: {}", request.getRequestURI());
        log.info("Method: {}", request.getMethod());

        // Log ALL headers
        log.info("--- ALL HEADERS RECEIVED ---");
        Enumeration<String> allHeaders = request.getHeaderNames();
        while (allHeaders.hasMoreElements()) {
            String name = allHeaders.nextElement();
            String value = request.getHeader(name);
            log.info("HEADER: {} = {}", name, value);
        }
        log.info("--- END HEADERS ---");

        try {
            String userId = request.getHeader("X-Auth-User-Id");
            String userName = request.getHeader("X-Auth-User-Name");
            String userRoles = request.getHeader("X-Auth-User-Roles");

            log.info("EXTRACTED AUTH HEADERS:");
            log.info("  X-Auth-User-Id = {}", userId);
            log.info("  X-Auth-User-Name = {}", userName);
            log.info("  X-Auth-User-Roles = {}", userRoles);

            // Check if headers exist
            boolean hasUserId = userId != null && !userId.isEmpty();
            boolean hasUserRoles = userRoles != null && !userRoles.isEmpty();

            log.info("HEADER CHECK: hasUserId={}, hasUserRoles={}", hasUserId, hasUserRoles);

            if (hasUserId && hasUserRoles) {
                log.info("✅ HEADERS FOUND - Processing authentication...");

                Collection<SimpleGrantedAuthority> authorities = Arrays.stream(userRoles.split(","))
                        .map(String::trim)
                        .peek(role -> log.info("  ROLE PARSED: {}", role))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

                log.info("✅ AUTHORITIES CREATED: {}", authorities);

                String principal = (userName != null && !userName.isEmpty()) ? userName : userId;
                log.info("✅ PRINCIPAL SET: {}", principal);

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(principal, null, authorities);
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authToken);
                log.info("✅✅✅ AUTHENTICATION SUCCESSFULLY SET IN SECURITY CONTEXT ✅✅✅");
                log.info("   User: {}", principal);
                log.info("   Authorities: {}", authorities);

            } else {
                log.warn("❌❌❌ HEADERS NOT FOUND ❌❌❌");
                log.warn("   userId is null/empty: {}", userId == null || userId.isEmpty());
                log.warn("   userRoles is null/empty: {}", userRoles == null || userRoles.isEmpty());
                log.warn("   Authentication NOT set - request will require further auth");
            }

        } catch (Exception e) {
            log.error("❌❌❌ ERROR IN JWT FILTER ❌❌❌", e);
            SecurityContextHolder.clearContext();
        }

        log.info("========== JWT FILTER COMPLETED ==========");
        filterChain.doFilter(request, response);
    }
}