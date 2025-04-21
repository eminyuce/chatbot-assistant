package com.yuce.chat.assistant.service;

import com.yuce.chat.assistant.model.AuthRequest;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class JwtService {

    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    private static final long EXPIRATION_TIME = 864_000_000; // 10 days in milliseconds
    private static final String ROLE_ANGULAR = "ROLE_ANGULAR";

    public String generateTokenAngularRole(AuthRequest authRequest) {
        Duration expiryDate = authRequest.isRememberMe()
                ? Duration.ofDays(14) // 2 weeks
                : Duration.ofHours(1); // default 1 hour
        // Use expiry to set expiration in your claims (e.g., via io.jsonwebtoken)
        return generateToken(authRequest.getUsername(), Collections.singletonList(ROLE_ANGULAR), expiryDate);
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
        try {
            Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}