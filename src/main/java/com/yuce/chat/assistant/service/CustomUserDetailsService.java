package com.yuce.chat.assistant.service;

import com.yuce.chat.assistant.persistence.entity.UserEntity;
import com.yuce.chat.assistant.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (username == null || username.trim().isEmpty()) {
            throw new UsernameNotFoundException("Username cannot be null or empty");
        }

        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("No user found with username: " + username));

        List<SimpleGrantedAuthority> authorities = user.getRoles() != null
                ? Arrays.stream(user.getRoles().split(","))
                .filter(role -> !role.trim().isEmpty())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList())
                : Collections.emptyList();

        return new User(user.getUsername(), user.getPassword(), authorities);
    }
}