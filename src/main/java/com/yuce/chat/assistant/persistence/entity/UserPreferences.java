package com.yuce.chat.assistant.persistence.entity;

import jakarta.persistence.*;

@Table(name = "USER_PREFERENCES")
public record UserPreferences(
        @Id
        Integer id,
        String category
) { }
