package com.yuce.chat.assistant.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Chatbot Assistant API")
                        .version("1.0")
                        .description("""
                                A Java Spring Boot-based Chatbot Assistant powered by a locally running Large Language Model (LLM) using Ollama, and integrated with Spring AI.
                                                                
                                This assistant intelligently detects user intent and responds with context-aware information, such as weather updates or stock prices. Designed for flexibility, it supports two distinct service modes to enable experimentation and extendability.

                                ✨ **Features**
                                - 🔁 *Locally Hosted LLM*: Runs an LLM via Ollama for fast, private, offline responses.
                                - 🎯 *Intent Detection*: Extracts and understands user intent using LLM, enabling accurate service routing.
                                """));
    }
}
