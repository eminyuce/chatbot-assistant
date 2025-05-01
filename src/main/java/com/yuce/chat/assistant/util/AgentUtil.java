package com.yuce.chat.assistant.util;


import org.springframework.ai.ollama.api.OllamaOptions;

import java.util.List;

public class AgentUtil {

    public static OllamaOptions createFunctionOptions(String... functions) {
        return OllamaOptions.builder().toolNames(functions).build();
    }

    public static String combinedQuery(List<String> queries) {
        return String.join(",", queries);
    }
}
