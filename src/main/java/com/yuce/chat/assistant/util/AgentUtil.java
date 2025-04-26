package com.yuce.chat.assistant.util;


import org.springframework.ai.ollama.api.OllamaOptions;

import java.util.List;

public class AgentUtil {

    public static OllamaOptions createFunctionOptions(String... functions) {
        var builder = OllamaOptions.builder();
        for (String function : functions) {
            builder = builder.function(function);
        }
        return builder.build();
    }

    public static String combinedQuery(List<String> queries) {
        return String.join(",", queries);
    }
}
