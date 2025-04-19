package com.yuce.chat.assistant.config;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Profile("dev")
@Configuration
public class DevBeanConfig {

    @Value("${spring.ai.ollama.base-url:http://localhost:11434}")
    private String ollamaBaseUrl;

    @Value("${spring.ai.ollama.model:llama3}")
    private String ollamaModel;

    @Bean("ollamaChatModel")
    @Primary
    public ChatModel ollamaChatModel() {
        return OllamaChatModel.builder()
                .ollamaApi(new OllamaApi(ollamaBaseUrl))
                .build();
    }
}
