package com.yuce.chat.assistant.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.yuce.chat.assistant.persistence.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@Slf4j
public class AppConfig {
    @Autowired
    private UserRepository userRepository;

    @Value("${Alpha.host}")
    private String alphaHost;

    //This bean runs after the full Spring context is ready, and is more reliable for DB initialization.
    @Bean
    public CommandLineRunner afterSpringReady() {
        return args -> {
            log.info("This bean runs after the full Spring context is ready.");
        };
    }

    @Bean
    public WebClient webClient() {
        return WebClient.builder().baseUrl(alphaHost).build();
    }

    private InMemoryUserDetailsManager getInMemoryUserDetailsManager() {
        var user = User.builder()
                .username("angular-user")
                .password(passwordEncoder().encode("angular-pass"))
                .roles("ANGULAR")
                .build();

        var admin = User.builder()
                .username("admin-user")
                .password(passwordEncoder().encode("admin-pass"))
                .roles("ANGULAR", "ADMIN")
                .build();

        return new InMemoryUserDetailsManager(user, admin);
    }

    @Bean
    public UserDetailsService userDetailsService() {
//          return new CustomUserDetailsService(userRepository);
        return getInMemoryUserDetailsManager();
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
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //  objectMapper.setDefaultSetterInfo(JsonSetter.Value.forValueNulls(Nulls.AS_EMPTY));
        return objectMapper;
    }
}
