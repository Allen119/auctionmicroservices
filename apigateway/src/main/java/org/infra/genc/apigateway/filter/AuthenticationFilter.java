package org.infra.genc.apigateway.filter;

import org.infra.genc.apigateway.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import java.util.List;

@Component
@Slf4j
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    // These constants are used for injecting trusted headers
    public static final String AUTH_USER_ID_HEADER = "X-Auth-User-Id";
    public static final String AUTH_USER_ROLES_HEADER = "X-Auth-User-Roles";

    private final RouteValidator validator;
    private final JwtUtil jwtUtil;

    public AuthenticationFilter(RouteValidator validator, JwtUtil jwtUtil) {
        super(Config.class);
        this.validator = validator;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            // 1. Check if the route is marked as secured by the RouteValidator
            if (validator.isSecured.test(request)) {

                var headers = request.getHeaders();
                List<String> authValues = headers.getOrEmpty(HttpHeaders.AUTHORIZATION);

                if (authValues.isEmpty()) {
                    log.warn("Missing Authorization header for request {}", request.getURI());
                    return setUnauthorizedResponse(response);
                }

                String authHeader = authValues.get(0);
                if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
                    log.warn("Invalid Authorization header format for request {}", request.getURI());
                    return setUnauthorizedResponse(response);
                }

                String token = authHeader.substring(7);
                try {
                    // 2. Validate Token and Extract Claims
                    jwtUtil.validateToken(token);
                    Claims claims = jwtUtil.extractAllClaims(token);

                    String userId = claims.getSubject();
                    String userRoles = claims.get("roles", String.class); // Get roles claim

                    // 3. Inject Trusted Headers for Downstream Services
                    ServerHttpRequest modifiedRequest = request.mutate()
                            .header(AUTH_USER_ID_HEADER, userId)
                            .header(AUTH_USER_ROLES_HEADER, userRoles != null ? userRoles : "GUEST")
                            .build();

                    // Replace the original exchange with the modified request
                    exchange = exchange.mutate().request(modifiedRequest).build();

                } catch (Exception e) {
                    log.warn("Token validation failed for request {}: {}", request.getURI(), e.getMessage());
                    return setUnauthorizedResponse(response);
                }
            }

            log.debug("Authentication/Authorization Context passed for {}", request.getURI());
            return chain.filter(exchange);
        };
    }

    private reactor.core.publisher.Mono<Void> setUnauthorizedResponse(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return response.setComplete();
    }

    public static class Config {
        // Configuration placeholder
    }
}
