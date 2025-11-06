package com.onepiece.user_service.service;


import com.onepiece.user_service.exception.InvalidTokenException;
import com.onepiece.user_service.model.UserPrincipal;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class JwtService {

    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

    private Set<String> blacklistedTokens = ConcurrentHashMap.newKeySet();

    private static final long JWT_EXPIRATION = 24 * 60 * 60 * 1000;

    private static final long REFRESH_TOKEN_EXPIRATION = 7 * 24 * 60 * 60 * 1000;

    @Value("${spring.jwt.secret}")
    private String SECRET_KEY_STRING;


    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username, JWT_EXPIRATION);
    }

    public String generateToken(UserDetails userDetails) {
        try {
            Map<String, Object> claims = new HashMap<>();

            List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());
            claims.put("roles", roles);

            if (userDetails instanceof UserPrincipal) {
                UserPrincipal userPrincipal = (UserPrincipal) userDetails;
                claims.put("userId", userPrincipal.getUserId());
            }

            claims.put("tokenType", "ACCESS");

            return createToken(claims, userDetails.getUsername(), JWT_EXPIRATION);
        } catch (Exception e) {
            logger.error("Error generating JWT token: {}", e.getMessage());
            throw new RuntimeException("Failed to generate JWT token", e);
        }
    }

    public String generateRefreshToken(UserDetails userDetails) {
        try {
            Map<String, Object> claims = new HashMap<>();
            claims.put("tokenType", "REFRESH");

            if (userDetails instanceof UserPrincipal) {
                UserPrincipal userPrincipal = (UserPrincipal) userDetails;
                claims.put("userId", userPrincipal.getUserId());
            }

            return createToken(claims, userDetails.getUsername(), REFRESH_TOKEN_EXPIRATION);
        } catch (Exception e) {
            logger.error("Error generating refresh token: {}", e.getMessage());
            throw new RuntimeException("Failed to generate refresh token", e);
        }
    }

    private String createToken(Map<String, Object> claims, String subject, long expiration) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .issuer("GSA-Auction-System")
                .signWith(getKey())
                .compact();
    }


    private SecretKey getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY_STRING);
        return Keys.hmacShaKeyFor(keyBytes);
    }


    public String extractUsername(String token) {
        try {
            return extractClaim(token, Claims::getSubject);
        } catch (Exception e) {
            logger.error("Error extracting username from token: {}", e.getMessage());
            throw new InvalidTokenException("Invalid token: unable to extract username");
        }
    }


    public List<String> extractRoles(String token) {
        try {
            Claims claims = extractAllClaims(token);
            @SuppressWarnings("unchecked")
            List<String> roles = (List<String>) claims.get("roles");
            return roles != null ? roles : new ArrayList<>();
        } catch (Exception e) {
            logger.error("Error extracting roles from token: {}", e.getMessage());
            return new ArrayList<>();
        }
    }


    public Integer extractUserId(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return (Integer) claims.get("userId");
        } catch (Exception e) {
            logger.error("Error extracting user ID from token: {}", e.getMessage());
            return null;
        }
    }


    public String extractTokenType(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return (String) claims.get("tokenType");
        } catch (Exception e) {
            logger.error("Error extracting token type from token: {}", e.getMessage());
            return null;
        }
    }


    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }


    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            logger.warn("JWT token is expired: {}", e.getMessage());
            throw new InvalidTokenException("Token has expired");
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
            throw new InvalidTokenException("Unsupported token format");
        } catch (MalformedJwtException e) {
            logger.error("JWT token is malformed: {}", e.getMessage());
            throw new InvalidTokenException("Malformed token");
        } catch (SignatureException e) {
            logger.error("JWT signature validation failed: {}", e.getMessage());
            throw new InvalidTokenException("Invalid token signature");
        } catch (IllegalArgumentException e) {
            logger.error("JWT token compact of handler are invalid: {}", e.getMessage());
            throw new InvalidTokenException("Invalid token format");
        }
    }


    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            if (isTokenBlacklisted(token)) {
                logger.warn("Token is blacklisted");
                return false;
            }

            final String username = extractUsername(token);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (Exception e) {
            logger.error("Token validation failed: {}", e.getMessage());
            return false;
        }
    }


    private boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (Exception e) {
            logger.error("Error checking token expiration: {}", e.getMessage());
            return true;
        }
    }


    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }


    public void blacklistToken(String token) {
        if (token != null && !token.isEmpty()) {
            blacklistedTokens.add(token);
            logger.info("Token blacklisted successfully");
        }
    }


    public boolean isTokenBlacklisted(String token) {
        return blacklistedTokens.contains(token);
    }


    public void cleanupExpiredTokens() {
        try {
            blacklistedTokens.removeIf(token -> {
                try {
                    return isTokenExpired(token);
                } catch (Exception e) {
                    // If we can't parse the token, remove it from blacklist
                    return true;
                }
            });
            logger.info("Expired tokens cleaned up from blacklist");
        } catch (Exception e) {
            logger.error("Error cleaning up expired tokens: {}", e.getMessage());
        }
    }


    public long getTokenExpirationTime() {
        return JWT_EXPIRATION;
    }


    public long getRefreshTokenExpirationTime() {
        return REFRESH_TOKEN_EXPIRATION;
    }
}
