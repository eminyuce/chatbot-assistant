package com.yuce.chat.assistant.model;


import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IChatMessage {
    private String id;
    private String chatId;
    private String userId;
    private String userName;
    private String content;
    private String prompt;
    private ChatRole role;
    private LocalDateTime timestamp;
}
