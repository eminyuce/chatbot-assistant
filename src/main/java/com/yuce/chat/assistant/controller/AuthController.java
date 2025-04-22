package com.yuce.chat.assistant.controller;

import com.yuce.chat.assistant.model.AuthRequest;
import com.yuce.chat.assistant.model.AuthResponse;
import com.yuce.chat.assistant.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
@Slf4j
@AllArgsConstructor
public class AuthController {
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @PostMapping(value = "/login-angular", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest authRequest, HttpServletRequest request, HttpServletResponse response) {
        log.info("Login attempt for user: {}", authRequest.getUsername());
        // In a real application, you would validate credentials against a database
        try {

            // Authenticate user against hardcoded credentials
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );
            if (authentication.isAuthenticated()) {
                String jwtToken = jwtService.generateTokenAngularRole(authRequest);
                List<String> roles = authentication.getAuthorities().stream()
                        .map(grantedAuthority -> grantedAuthority.getAuthority())
                        .collect(Collectors.toList());
                return ResponseEntity.ok(new AuthResponse(jwtToken, roles));
            } else {
                return ResponseEntity.badRequest().body(new AuthResponse(null, null));
            }
        } catch (Exception e) {
            log.error("Login failed for user: {}", authRequest.getUsername(), e);
            return ResponseEntity.badRequest().body(new AuthResponse(null, null));
        }
    }
}