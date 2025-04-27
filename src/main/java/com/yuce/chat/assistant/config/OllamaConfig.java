package com.yuce.chat.assistant.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.management.ModelManagementOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import java.util.List;

@Profile("dev")
@Configuration
public class OllamaConfig {

    @Value("${spring.ai.ollama.base-url:http://localhost:11434}")
    private String ollamaBaseUrl;

    @Value("${spring.ai.ollama.model:llama3}")
    private String ollamaModel;

    @Bean("ollamaChatModel")
    @Primary
    public ChatModel ollamaChatModel() {
        return OllamaChatModel.builder()
                .modelManagementOptions(ModelManagementOptions.builder().additionalModels(List.of(ollamaModel)).build())
                .ollamaApi(new OllamaApi(ollamaBaseUrl))
                .build();
    }

    @Bean
    public ChatClient chatClient(ChatModel chatModel) {
        return ChatClient.builder(chatModel).defaultAdvisors(List.of(new SimpleLoggerAdvisor())).build();
    }

}
