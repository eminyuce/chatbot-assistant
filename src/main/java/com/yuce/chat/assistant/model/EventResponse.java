package com.yuce.chat.assistant.model;

import lombok.*;
import lombok.experimental.Accessors;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
public class EventResponse {
    String content;
}
