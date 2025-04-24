package com.yuce.chat.assistant.service.impl;


import com.yuce.chat.assistant.model.AuthRequest;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.netty.util.internal.StringUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class JwtService {

    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    private static final long EXPIRATION_TIME = 864_000_000; // 10 days in milliseconds

    public String generateToken(Authentication authentication, AuthRequest authRequest) {
        Duration expiryDate = authRequest.isRememberMe()
                ? Duration.ofDays(14) // 2 weeks
                : Duration.ofHours(1); // default 1 hour
        // Use expiry to set expiration in your claims (e.g., via io.jsonwebtoken)
        List<String> roles = authentication.getAuthorities().stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .collect(Collectors.toList());
        return generateToken(authRequest.getUsername(), roles, expiryDate);
    }

    public String generateToken(String username, List<String> roles, Duration expiry) {

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiry.toMillis());

        return Jwts.builder()
                .setSubject(username)
                .claim("roles", roles) // Add ROLE_ANGULAR as a claim
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // Helper method to get token from HTTP request
    private String getTokenFromRequest() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getCredentials() == null) {
            return null;  // No token found
        }
        return (String) authentication.getCredentials();  // Assuming the token is stored as credentials
    }

    public List<String> getRolesFromToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Collections.emptyList();
        }
        String token = authHeader.substring(7);
        return this.getRolesFromToken(token);
    }

    @SuppressWarnings("unchecked")
    public List<String> getRolesFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("roles", List.class);
    }

    public boolean validateToken(String token) {
        if (StringUtil.isNullOrEmpty(token)) {
            return false;
        }
        try {
            Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

}