package com.yuce.chat.assistant.service.impl;

import com.yuce.chat.assistant.model.Event;
import com.yuce.chat.assistant.model.EventResponse;
import com.yuce.chat.assistant.model.IntentExtractionResult;
import com.yuce.chat.assistant.persistence.entity.UserEntity;
import com.yuce.chat.assistant.persistence.repository.UserRepository;
import com.yuce.chat.assistant.service.IntentService;
import com.yuce.chat.assistant.util.Constants;
import com.yuce.chat.assistant.util.FormatTextUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
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

@Service("chat_bot_users-service")
@Slf4j
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService, IntentService {

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

    public Event getChatBotUsers(IntentExtractionResult intent) {
        if (intent.hasAccessRole("ROLE_ADMIN")) {
            // Define the sorting criterion (e.g., sorting by 'username' in ascending order)
            Sort sort = Sort.by(Sort.Order.asc("username"));

            // Fetch users with sorting applied
            var users = userRepository.findAll(sort);

            return Event.builder()
                    .type(Constants.CHAT_BOT_USERS)
                    .eventResponse(EventResponse.builder()
                            .content(FormatTextUtil.formatUsersResponse(users))
                            .build())
                    .build();
        } else {
            return Event.builder()
                    .type(Constants.CHAT_BOT_USERS)
                    .eventResponse(EventResponse.builder()
                            .content("Only ADMIN roles user can access these information")
                            .build())
                    .build();
        }
    }

    @Override
    public Event run(IntentExtractionResult intent) {
        return this.getChatBotUsers(intent);
    }
}