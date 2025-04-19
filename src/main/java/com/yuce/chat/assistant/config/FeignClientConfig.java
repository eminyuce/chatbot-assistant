package com.yuce.chat.assistant.config;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignClientConfig {

    @Value("${weather-api-key:1234}")
    private String weatherApiKey;

    @Value("${stock-api-key:9876}")
    private String stockApiKey;

    @Bean
    public RequestInterceptor dynamicRequestInterceptor() {
        return requestTemplate -> {
            String clientName = requestTemplate.feignTarget().name();
            switch (clientName) {
                case "weatherClient":
                    requestTemplate.header("X-API-KEY", weatherApiKey);
                    break;
                case "stockClient":
                    requestTemplate.header("X-API-KEY", stockApiKey);
                    break;
                default:
                    throw new IllegalArgumentException("No FEIGN CONFIG is defined for " + clientName);
            }
        };
    }

}
