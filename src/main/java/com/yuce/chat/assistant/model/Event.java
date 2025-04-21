package com.yuce.chat.assistant.model;

import lombok.Builder;
import lombok.experimental.Accessors;

@Builder
@Accessors(chain = true)
public record Event(String type, EventResponse eventResponse) {

}

