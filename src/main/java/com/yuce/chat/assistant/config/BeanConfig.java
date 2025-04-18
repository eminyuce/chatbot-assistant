package com.yuce.chat.assistant.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.management.ModelManagementOptions;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.ai.ollama.OllamaEmbeddingModel; // Adjust based on actual class
import io.micrometer.observation.ObservationRegistry;
import org.springframework.ai.ollama.api.OllamaOptions;

@Configuration
public class BeanConfig {

    // ------------------- DEV: OLLAMA -------------------

    @Value("${spring.ai.ollama.base-url:http://localhost:11434}")
    private String ollamaBaseUrl;

    @Value("${spring.ai.ollama.model:llama3}")
    private String ollamaModel;
    @Bean("customOllamaChatModel")
    @Profile("dev")
    public ChatModel ollamaChatModel() {
        return OllamaChatModel.builder()
                .ollamaApi(new OllamaApi(ollamaBaseUrl))
                .build();
    }

    @Bean("customOllamaEmbeddingModel")
    @Profile("dev")
    public EmbeddingModel ollamaEmbeddingModel(@Value("${spring.ai.ollama.base-url:http://localhost:11434}") String ollamaBaseUrl,
                                               @Value("${spring.ai.ollama.model:llama3}") String ollamaModel) {
        OllamaApi ollamaApi = new OllamaApi(ollamaBaseUrl);
        OllamaOptions options = OllamaOptions.builder().model(ollamaModel).build();
        ObservationRegistry observationRegistry = ObservationRegistry.create();
        ModelManagementOptions modelManagementOptions = ModelManagementOptions.builder()
                .pullModelStrategy(ModelManagementOptions.defaults().pullModelStrategy())
                .build();

        return new OllamaEmbeddingModel(ollamaApi, options, observationRegistry, modelManagementOptions);
    }
    @Bean("customOllamaChatClient")
    @Profile("dev")
    public ChatClient ollamaChatClient(ChatModel ollamaChatModel) {
        return ChatClient.builder(ollamaChatModel)
                .defaultSystem("You are a helpful assistant.")
                .build();
    }

    // ------------------- PROD: OPENAI -------------------
    @Value("${spring.ai.openai.api-key}")
    private String openAiApiKey;

    @Value("${spring.ai.openai.model:gpt-3.5-turbo}")
    private String openAiModel;

    @Value("${spring.ai.openai.base-url:https://api.openai.com/v1}")
    private String openAiBaseUrl;

    @Bean
    @Profile("prod")
    public ChatModel openAiChatModel() {
        OpenAiApi openAiApi =  OpenAiApi.builder()
                .baseUrl(openAiBaseUrl)
                .apiKey(openAiApiKey)
                .build();

        return OpenAiChatModel.builder()
                .openAiApi(openAiApi)
                .defaultOptions(OpenAiChatOptions.builder().model(openAiModel).build())
                .build();
    }

    @Bean
    @Profile("prod")
    public ChatClient openAiChatClient(ChatModel openAiChatModel) {
        return ChatClient.builder(openAiChatModel)
                .defaultSystem("You are a helpful assistant.")
                .build();
    }
}
