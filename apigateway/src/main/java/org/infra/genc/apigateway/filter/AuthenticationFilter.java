package org.infra.genc.apigateway.filter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.infra.genc.apigateway.util.JwtUtil;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private final JwtUtil jwtUtil;

    public AuthenticationFilter(JwtUtil jwtUtil) {
        super(Config.class);
        this.jwtUtil = jwtUtil;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            if (isSecured(request)) {
                String authHeader = request.getHeaders().getFirst("Authorization");

                // ✅ Check Authorization header exists
                if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                    return setUnauthorizedResponse(response, "Missing or invalid Authorization header");
                }

                String token = authHeader.substring(7);

                try {
                    if (!jwtUtil.validateToken(token)) {
                        return setUnauthorizedResponse(response, "Invalid or expired token");
                    }

                    Integer userId = jwtUtil.extractUserId(token);           // "userId": 8
                    String userName = jwtUtil.extractUsername(token);        // "sub": "@Allen"
                    String roles = jwtUtil.extractRolesFromToken(token);     // "roles": "ROLE_BUYER,ROLE_ADMIN"

                    log.info("Token validation passed for user: {} (ID: {})", userName, userId);

                    // ✅ CRITICAL: Check if user has roles (authorization requirement)
                    if (roles == null || roles.trim().isEmpty()) {
                        log.warn("UNAUTHORIZED: User {} (ID: {}) has NO ROLES assigned", userName, userId);
                        return setUnauthorizedResponse(response, "User has no roles assigned - authorization required");
                    }

                    if (userId == null || userId <= 0) {
                        log.warn("UNAUTHORIZED: Invalid userId for user: {}", userName);
                        return setUnauthorizedResponse(response, "Invalid user ID in token");
                    }

                    log.info("✓ Authorization successful");
                    log.info("  - Username: {} (sub claim)", userName);
                    log.info("  - User ID: {}", userId);
                    log.info("  - Roles: {}", roles);

                    ServerHttpRequest modifiedRequest = request.mutate()
                            .header("X-Auth-User-Id", userId.toString())
                            .header("X-Auth-User-Name", userName)
                            .header("X-Auth-User-Roles", roles)
                            .build();

                    log.debug("✓ Authorization headers injected: userId={}, userName={}, roles={}",
                            userId, userName, roles);

                    exchange = exchange.mutate().request(modifiedRequest).build();

                } catch (Exception e) {
                    log.error("Token validation failed: {}", e.getMessage(), e);
                    return setUnauthorizedResponse(response, "Token validation failed");
                }
            }

            return chain.filter(exchange);
        };
    }

    private boolean isSecured(ServerHttpRequest request) {
        String path = request.getURI().getPath();
        return !(path.contains("/auth/login") ||
                path.contains("/auth/register") ||
                path.contains("/actuator") ||
                path.contains("/swagger-ui") ||
                path.contains("/v3/api-docs"));
    }

    private Mono<Void> setUnauthorizedResponse(ServerHttpResponse response, String message) {
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        log.warn("❌ Unauthorized: {}", message);
        return response.setComplete();
    }

    public static class Config {
    }
}