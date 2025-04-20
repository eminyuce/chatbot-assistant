package com.yuce.chat.assistant.model;


import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record IChatMessage(String id,
                           String chatId,
                           String userId,
                           String userName,
                           String content,
                           String prompt,
                           ChatRole role,
                           LocalDateTime timestamp) {
}
