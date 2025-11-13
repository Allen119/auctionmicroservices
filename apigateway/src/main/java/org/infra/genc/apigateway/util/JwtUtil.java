package org.infra.genc.apigateway.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;

@Component
@Slf4j
public class JwtUtil {

    @Value("${genc.jwt.secret}")
    private String secret;

    @Value("${genc.jwt.expiration:900000}")
    private Long expiration;

    private SecretKey getSigningKey() {
        log.debug("Generating signing key from Base64 secret");
        try {
            byte[] decodedKey = Base64.getDecoder().decode(secret);
            log.debug("✓ Secret decoded from Base64");
            SecretKey key = Keys.hmacShaKeyFor(decodedKey);
            log.debug("✓ Signing key generated successfully");
            return key;
        } catch (IllegalArgumentException e) {
            log.error("❌ Invalid Base64 secret: {}", e.getMessage());
            throw new RuntimeException("Invalid JWT secret key configuration - must be Base64 encoded", e);
        }
    }

    public String extractUsername(String token) {
        log.debug("Extracting username from 'sub' claim");
        try {
            String username = extractClaim(token, Claims::getSubject);
            if (username != null && !username.isEmpty()) {
                log.debug("✓ Extracted username: {}", username);
                return username;
            }
            log.warn("Username not found in token (sub claim)");
            return null;
        } catch (Exception e) {
            log.error("Error extracting username: {}", e.getMessage());
            return null;
        }
    }

    public String extractUserName(String token) {
        return extractUsername(token);  // ← Gets the 'sub' claim (@Allen)
    }

    public Integer extractUserId(String token) {
        log.debug("Extracting userId from token");
        try {
            Claims claims = extractAllClaims(token);
            Object userIdObj = claims.get("userId");
            if (userIdObj != null) {
                if (userIdObj instanceof Integer) {
                    return (Integer) userIdObj;
                } else if (userIdObj instanceof Long) {
                    return ((Long) userIdObj).intValue();
                } else if (userIdObj instanceof Double) {
                    return ((Double) userIdObj).intValue();
                } else {
                    return Integer.parseInt(userIdObj.toString());
                }
            }
            log.warn("userId not found in token claims");
            return null;
        } catch (Exception e) {
            log.error("Error extracting userId from token: {}", e.getMessage());
            return null;
        }
    }

    public Date extractExpiration(String token) {
        log.debug("Extracting expiration from token");
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    public String extractCustomClaim(String token, String claimName) {
        log.debug("Extracting custom claim: {}", claimName);
        try {
            Claims claims = extractAllClaims(token);
            Object claimValue = claims.get(claimName);
            if (claimValue != null) {
                String value = claimValue.toString();
                log.debug("✓ Extracted {}: {}", claimName, value);
                return value;
            }
            log.warn("Claim {} not found in token", claimName);
            return null;
        } catch (Exception e) {
            log.error("Error extracting claim {}: {}", claimName, e.getMessage());
            return null;
        }
    }

    public Claims extractAllClaims(String token) {
        log.debug("Extracting all claims from token");
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            log.error("Error parsing JWT claims: {}", e.getMessage());
            throw e;
        }
    }

    private Boolean isTokenExpired(String token) {
        try {
            Date expirationDate = extractExpiration(token);
            boolean expired = expirationDate.before(new Date());
            log.debug("Token expired status: {}", expired);
            return expired;
        } catch (Exception e) {
            log.error("Error checking token expiration: {}", e.getMessage());
            return true;
        }
    }

    public String extractRolesFromToken(String token) {
        log.debug("Extracting roles from token");
        try {
            Claims claims = extractAllClaims(token);
            Object rolesObj = claims.get("roles");
            if (rolesObj != null) {
                String roles = rolesObj.toString().trim();
                log.debug("✓ Extracted roles from token: {}", roles);
                return roles;
            }
            log.warn("roles not found in token claims");
            return "";
        } catch (Exception e) {
            log.error("Error extracting roles from token: {}", e.getMessage());
            return "";
        }
    }

    public Boolean validateToken(String token) {
        try {
            log.debug("Validating token...");

            String username = extractUsername(token);
            if (username == null || username.isEmpty()) {
                log.warn("Token missing subject (username)");
                return false;
            }
            log.debug("Token username: {}", username);

            boolean isExpired = isTokenExpired(token);
            if (isExpired) {
                log.warn("Token is expired for user: {}", username);
                return false;
            }

            log.info("✓ Token validation successful for user: {}", username);
            return true;

        } catch (Exception e) {
            log.error("Exception in validateToken: {}", e.getMessage(), e);
            return false;
        }
    }
}