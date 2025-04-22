package com.yuce.chat.assistant.model;

import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private List<String> roles;
}