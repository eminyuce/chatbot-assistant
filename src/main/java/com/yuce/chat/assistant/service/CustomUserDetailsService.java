package com.yuce.chat.assistant.service;

import com.yuce.chat.assistant.persistence.entity.UserEntity;
import com.yuce.chat.assistant.persistence.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@Slf4j
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByUsername(username).orElseThrow(null);
        if (user != null) {
            var authorities = Arrays.stream(user.getRoles().split(","))
                    .map(SimpleGrantedAuthority::new)
                    .toList();

            return new User(user.getUsername(), user.getPassword(), authorities);
        } else {
            var angularUser = User.builder()
                    .username("angular-user")
                    .password(passwordEncoder.encode("angular-pass"))
                    .roles("ANGULAR")
                    .build();

            var admin = User.builder()
                    .username("admin-user")
                    .password(passwordEncoder.encode("admin-pass"))
                    .roles("ANGULAR", "ADMIN")
                    .build();
            return angularUser.getUsername().equals(username) ? angularUser : admin;
        }

    }
}