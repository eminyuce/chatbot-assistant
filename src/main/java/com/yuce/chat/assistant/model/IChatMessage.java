package com.yuce.chat.assistant.model;


import lombok.*;

import java.util.List;


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
    private String timestamp;
    private List<String> chatBotRoles;
}
