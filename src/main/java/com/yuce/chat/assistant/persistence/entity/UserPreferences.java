package com.yuce.chat.assistant.persistence.entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Table(name = "USER_PREFERENCES")
public record UserPreferences(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        Integer id,
        String category
) {
}
