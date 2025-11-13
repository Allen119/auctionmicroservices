package org.infra.genc.apigateway.filter;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.function.Predicate;

@Component
public class RouteValidator {
    public static final List<String> openApiEndpoints = List.of(
            // ========== Auth Service - Public Endpoints ==========
            "/api/v1/userservice/auth/login",
            "/api/v1/userservice/auth/register",

            // ========== Service Discovery ==========
            "/eureka",

            // ========== Monitoring & Documentation ==========
            "/actuator/health",
            "/v3/api-docs",
            "/swagger-ui"
    );

    /**
     * Predicate to determine if a route requires authentication
     *
     * Returns TRUE = Route is SECURED (requires JWT token)
     * Returns FALSE = Route is PUBLIC (no JWT needed)
     *
     * Example:
     *   /api/v1/userservice/auth/login         → false (public)
     *   /api/v1/userservice/roles              → true (secured)
     *   /api/v1/userservice/users/profile      → true (secured)
     */
    public Predicate<ServerHttpRequest> isSecured =
            request -> openApiEndpoints
                    .stream()
                    .noneMatch(uri -> request.getURI().getPath().startsWith(uri));
    // ↑ Changed from .contains() to .startsWith() for precision
}