package com.yuce.chat.assistant.model;


import lombok.Data;

@Data
public class AuthRequest {
    private String username;
    private String password;
    private boolean rememberMe = true;
}
