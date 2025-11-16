package com.onepiece.paymentservice.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final List<String> SKIPPED_PATHS = List.of(
            "/actuator",
            "/v3/api-docs",
            "/swagger-ui"
    );


    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return SKIPPED_PATHS.stream().anyMatch(path::startsWith);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        String method = request.getMethod();

        // ========== CHECK 2: Client Requests (via API Gateway) ==========
        log.info("👤 Client Request detected: {} {}", method, path);

        String userId = request.getHeader("X-Auth-User-Id");
        String userName = request.getHeader("X-Auth-User-Name");
        String userRoles = request.getHeader("X-Auth-User-Roles");

        if (userId != null && !userId.isEmpty() && userRoles != null && !userRoles.isEmpty()) {
            try {
                Collection<SimpleGrantedAuthority> authorities = Arrays.stream(userRoles.split(","))
                        .map(String::trim)
                        .map(role -> {
                            if (!role.startsWith("ROLE_")) {
                                return new SimpleGrantedAuthority("ROLE_" + role);
                            }
                            return new SimpleGrantedAuthority(role);
                        })
                        .collect(Collectors.toList());

                String principal = (userName != null && !userName.isEmpty()) ? userName : userId;

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(principal, null, authorities);
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);

                log.info("✅ User {} authenticated with roles: {}", principal, authorities);
                filterChain.doFilter(request, response);
                return;

            } catch (Exception e) {
                log.error("❌ Error setting authentication: {}", e.getMessage());
                response.setStatus(401);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Unauthorized - Invalid JWT\"}");
                return;
            }
        } else {
            log.warn("❌ Request rejected - No JWT headers found");
            response.setStatus(401);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Unauthorized - Missing JWT Headers\"}");
        }
    }
}