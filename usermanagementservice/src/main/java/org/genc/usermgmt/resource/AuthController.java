package org.genc.usermgmt.resource;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.genc.usermgmt.dto.AuthRequestDTO;
import org.genc.usermgmt.dto.AuthResponseDTO;
import org.genc.usermgmt.dto.ErrorResponse;
import org.genc.usermgmt.dto.UserRegistrationRequestDTO;
import org.genc.usermgmt.service.api.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/userservice/auth")
@Tag(name = "Authentication", description = "User authentication and registration endpoints")
@Slf4j
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Login with username and password to get JWT token")
    public ResponseEntity<?> login(
            @Valid @RequestBody AuthRequestDTO request,
            HttpServletRequest servletRequest) {
        try {
            log.info("Login attempt for user: {}", request.getUsername());

            AuthResponseDTO response = authService.login(request);

            log.info("User '{}' logged in successfully", request.getUsername());
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.error("Invalid credentials for user: {}", request.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ErrorResponse.of(
                            HttpStatus.UNAUTHORIZED,
                            "Invalid username or password",
                            servletRequest.getRequestURI()
                    ));
        } catch (Exception e) {
            log.error("Authentication error for user: {}", request.getUsername(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ErrorResponse.of(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            "Authentication error occurred",
                            servletRequest.getRequestURI()
                    ));
        }
    }

    @PostMapping("/register")
    @Operation(summary = "User registration", description = "Register a new user as BUYER")
    public ResponseEntity<?> register(
            @Valid @RequestBody UserRegistrationRequestDTO request,
            HttpServletRequest servletRequest) {
        try {
            log.info("Registration attempt for username: {}", request.getUsername());

            AuthResponseDTO response = authService.register(request);

            log.info("User '{}' registered successfully", request.getUsername());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException e) {
            log.error("Registration validation error for username: {}", request.getUsername());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ErrorResponse.of(
                            HttpStatus.BAD_REQUEST,
                            e.getMessage(),
                            servletRequest.getRequestURI()
                    ));
        } catch (Exception e) {
            log.error("Registration error for username: {}", request.getUsername(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ErrorResponse.of(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            "Registration error occurred",
                            servletRequest.getRequestURI()
                    ));
        }
    }
}