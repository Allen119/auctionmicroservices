package org.genc.usermgmt.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.genc.usermgmt.dto.CustomUserDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JwtUtil {

    @Value("${genc.jwt.secret}")
    private String secret;

    @Value("${genc.jwt.expiration:900000}")
    private Long expiration;

    @Value("${genc.jwt.issuer:onepiece-auction}")
    private String issuer;

    @Value("${genc.jwt.audience:onepiece-users}")
    private String audience;

    private SecretKey getSigningKey() {
        log.debug("Generating signing key from Base64 secret");
        try {
            byte[] decodedKey = Base64.getDecoder().decode(secret);
            SecretKey key = Keys.hmacShaKeyFor(decodedKey);
            log.debug("✓ Signing key generated successfully");
            return key;
        } catch (IllegalArgumentException e) {
            log.error("❌ Invalid Base64 secret: {}", e.getMessage());
            throw new RuntimeException("Invalid JWT secret key configuration - must be Base64 encoded", e);
        }
    }

    public String extractUsername(String token) {
        log.debug("Extracting username from token");
        return extractClaim(token, Claims::getSubject);
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
                } else {
                    return Integer.parseInt(userIdObj.toString());
                }
            }
            log.warn("⚠️ userId not found in token claims");
            return null;
        } catch (Exception e) {
            log.error("❌ Error extracting userId from token: {}", e.getMessage());
            return null;
        }
    }

    public String extractUserName(String token) {
        log.debug("Extracting userName from token");
        try {
            Claims claims = extractAllClaims(token);
            String userName = claims.getSubject();
            if (userName != null && !userName.isEmpty()) {
                log.debug("✓ Extracted userName: {}", userName);
                return userName;
            }
            log.warn("⚠️ userName not found in token claims");
            return null;
        } catch (Exception e) {
            log.error("❌ Error extracting userName from token: {}", e.getMessage());
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

    private Claims extractAllClaims(String token) {
        log.debug("Extracting all claims from token");
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            log.error("❌ Error parsing JWT claims: {}", e.getMessage());
            throw e;
        }
    }

    private Boolean isTokenExpired(String token) {
        boolean expired = extractExpiration(token).before(new Date());
        log.debug("Token expired status: {}", expired);
        return expired;
    }

    public String generateToken(CustomUserDetails userDetails) {
        log.info("Generating JWT token for user: {} (ID: {})",
                userDetails.getUsername(), userDetails.getUserId());
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails);
    }

    private String createToken(Map<String, Object> claims, CustomUserDetails userDetails) {
        claims.put("userId", userDetails.getUserId());
        claims.put("roles", extractAndConcatenateRoles(userDetails));

        try {
            String token = Jwts.builder()
                    .claims(claims)
                    .subject(userDetails.getUsername())
                    .issuedAt(new Date(System.currentTimeMillis()))
                    .expiration(new Date(System.currentTimeMillis() + expiration))
                    .issuer(issuer)
                    .audience().add(audience).and()
                    .signWith(getSigningKey())
                    .compact();

            log.info("✓ JWT token created successfully for user: {} (ID: {}) with roles: {}",
                    userDetails.getUsername(), userDetails.getUserId(),
                    extractAndConcatenateRoles(userDetails));
            return token;
        } catch (Exception e) {
            log.error("❌ Error creating JWT token: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate JWT token", e);
        }
    }

    public Boolean validateToken(String token) {
        try {
            final String username = extractUsername(token);
            boolean isValid = !isTokenExpired(token);
            log.debug("✓ Token validation result for user {}: {}", username, isValid);
            return isValid;
        } catch (Exception e) {
            log.error("❌ Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    public String extractAndConcatenateRoles(CustomUserDetails userDetails) {
        Set<? extends GrantedAuthority> authorities = (Set<? extends GrantedAuthority>) userDetails.getAuthorities();

        if (authorities == null || authorities.isEmpty()) {
            log.warn("⚠️ No roles found for user: {}", userDetails.getUsername());
            return "";
        }

        String rolesString = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        log.debug("✓ Extracted roles for user {}: {}", userDetails.getUsername(), rolesString);
        return rolesString;
    }

    public String extractRolesFromToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            Object rolesObj = claims.get("roles");
            String roles = rolesObj != null ? rolesObj.toString() : "";
            log.debug("✓ Extracted roles from token: {}", roles);
            return roles;
        } catch (Exception e) {
            log.error("❌ Error extracting roles from token: {}", e.getMessage());
            return "";
        }
    }

    public void printTokenClaims(String token) {
        try {
            Claims claims = extractAllClaims(token);
            log.info("═══════════════════════════════════════════════════════════");
            log.info("JWT Token Claims for User: {}", claims.getSubject());
            log.info("═══════════════════════════════════════════════════════════");
            log.info("  - userId: {}", claims.get("userId"));
            log.info("  - userName: {}", claims.get("userName"));
            log.info("  - roles: {}", claims.get("roles"));
            log.info("  - Subject: {}", claims.getSubject());
            log.info("  - Issued At: {}", new Date(claims.getIssuedAt().getTime()));
            log.info("  - Expiration: {}", new Date(claims.getExpiration().getTime()));
            log.info("  - Issuer: {}", claims.getIssuer());
            log.info("  - Audience: {}", claims.getAudience());
            log.info("═══════════════════════════════════════════════════════════");
        } catch (Exception e) {
            log.error("Error printing token claims: {}", e.getMessage());
        }
    }
}