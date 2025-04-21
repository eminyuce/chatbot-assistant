package com.yuce.chat.assistant.filter;

import com.yuce.chat.assistant.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        String token = authHeader.substring(7);
        if(jwtService.validateToken(token)){
            String username = jwtService.getUsernameFromToken(token); // must validate token here
            System.out.println("Authentication set for user: " + username);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                List<String> roles = jwtService.getRolesFromToken(token);
                List<SimpleGrantedAuthority> authorities = roles.stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

                var authentication = new UsernamePasswordAuthenticationToken(username, null,                        authorities);
                var requestAttributeSecurityContextRepository = new RequestAttributeSecurityContextRepository();
                var securityContext = SecurityContextHolder.createEmptyContext();
                securityContext.setAuthentication(authentication);
                requestAttributeSecurityContextRepository.saveContext(securityContext, request, response);

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }else{
            log.error("JWT token is not valid");
        }
        filterChain.doFilter(request, response);
    }

}
