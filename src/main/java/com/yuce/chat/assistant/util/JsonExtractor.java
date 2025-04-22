package com.yuce.chat.assistant.util;

public class JsonExtractor {

    public static String extractJson(String input) {
        return input.replace("```json", "").replace("```", "").trim();
    }
}
