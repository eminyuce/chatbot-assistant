package com.yuce.chat.assistant.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;

@Profile("prod")
@Configuration
public class ProdBeanConfig {

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
