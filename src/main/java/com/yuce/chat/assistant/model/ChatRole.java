package com.yuce.chat.assistant.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ChatRole {
    USER, ASSISTANT;

    @JsonValue
    public int toValue() {
        return ordinal();
    }
}

