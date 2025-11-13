package org.genc.usermgmt.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.genc.usermgmt.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Handles security authentication errors by returning a 401 response with a custom JSON body.
 * This is triggered by the ExceptionTranslationFilter when an AuthenticationException occurs.
 *
 * Triggered scenarios:
 * - Missing JWT token in Authorization header
 * - Invalid or expired JWT token
 * - Malformed JWT token
 * - Failed password authentication
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        // Log the authentication error with request details for debugging
        log.error("Authentication failed for request: {} {} - Error: {}",
                request.getMethod(),
                request.getRequestURI(),
                authException.getMessage());

        // 1. Set the correct HTTP status and content type
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        // 2. Create the custom error response
        ErrorResponse errorResponse = ErrorResponse.of(
                HttpStatus.UNAUTHORIZED,
                "Unauthorized: " + authException.getMessage(),
                request.getRequestURI()
        );

        // 3. Write the JSON response body with error handling
        try {
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        } catch (IOException e) {
            log.error("Failed to write error response to client", e);
            response.getWriter().write("{\"error\": \"Authentication failed\"}");
        }
    }
}