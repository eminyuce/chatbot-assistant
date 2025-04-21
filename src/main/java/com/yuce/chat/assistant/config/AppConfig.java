package com.yuce.chat.assistant.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yuce.chat.assistant.persistence.entity.UserEntity;
import com.yuce.chat.assistant.persistence.repository.UserRepository;
import com.yuce.chat.assistant.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class AppConfig {
    private final UserRepository userRepository;

    //This bean runs after the full Spring context is ready, and is more reliable for DB initialization.
    @Bean
    public CommandLineRunner insertDefaultUsers() {
        return args -> {
            if (userRepository.findByUsername("angular-user").isEmpty()) {
                userRepository.save(UserEntity.builder()
                        .username("angular-user")
                        .password(passwordEncoder().encode("angular-pass"))
                        .roles("ANGULAR")
                        .build());
            }

            if (userRepository.findByUsername("admin-user").isEmpty()) {
                userRepository.save(UserEntity.builder()
                        .username("admin-user")
                        .password(passwordEncoder().encode("admin-pass"))
                        .roles("ANGULAR,ADMIN")
                        .build());
            }

            log.info("Inserted default users into the H2 database.");
        };
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailsService(userRepository);
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

}
