package com.yuce.chat.assistant;

import com.yuce.chat.assistant.service.GroqService;
import com.yuce.chat.assistant.service.ProductDescriptionService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.io.IOException;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.yuce.chat.assistant.feign")
@EnableJpaRepositories(basePackages = "com.yuce.chat.assistant.persistence.repository")
@EntityScan(basePackages = "com.yuce.chat.assistant.persistence.entity")
@EnableAspectJAutoProxy // <-- Make sure this is present
public class ChatbotServiceApplication {

    public static void main(String[] args) throws IOException, InterruptedException {
        ApplicationContext context =   SpringApplication.run(ChatbotServiceApplication.class, args);
        context.getBean(GroqService.class).generateDescriptions();
    }

}
