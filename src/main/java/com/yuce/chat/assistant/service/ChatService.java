package com.yuce.chat.assistant.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yuce.chat.assistant.model.*;
import com.yuce.chat.assistant.tool.AiToolService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ResponseEntity;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.yuce.chat.assistant.util.Constants.*;

@Service
@Slf4j
public class ChatService {

    @Autowired
    private ChatModel chatModel;

    @Autowired
    private ChatClient chatClient;

    @Value("classpath:/prompts/intent-message.st")
    private Resource intentMessageResource;

    @Autowired
    @Qualifier("static")
    private AiToolService aiToolService;

    @Autowired
    private ObjectMapper objectMapper;

    public Event getResponseStream(IChatMessage iChatMessage) {
        IntentResult intent = detectIntent(iChatMessage.getPrompt());
        switch (intent.getIntent()) {
            case WEATHER:
                return aiToolService.getWeather(intent);
            case STOCK_PRICE:
                return aiToolService.getStockPrice(intent);
            default:
                return getPromptCall(iChatMessage.getPrompt());
        }
    }

    private Event getPromptCall(String prompt) {
        try {
            String response = chatModel.call(prompt);
            return new Event(CHAT, EventResponse.builder().content(response).build());
        }catch (Exception e){
            return new Event(CHAT, EventResponse.builder().content(e.getMessage()).build());
        }
    }

    private IntentResult detectIntent(String prompt) {
        try {
            Prompt intentPrompt = new Prompt(List.of(
                    new SystemMessage(intentMessageResource),
                    new UserMessage(prompt)
            ));
            String jsonResponse = chatModel.call(intentPrompt).getResult().getOutput().getText();
            Map<String, Object> result = objectMapper.readValue(jsonResponse, Map.class);
            String intent = (String) result.getOrDefault("intent", "general");
            Map<String, Object> parameters = (Map<String, Object>) result.getOrDefault("parameters", Map.of());
            String city = parameters.get("city") != null ? parameters.get("city").toString() : null;
            String symbol = parameters.get("symbol") != null ? parameters.get("symbol").toString() : null;
            return new IntentResult(intent, new Parameters(city, symbol));
        } catch (Exception e) {
            // Fallback to general query if intent detection fails
            log.error("Detecting intention error",e);
            return new IntentResult("general", new Parameters(null, null));
        }
    }

    public Event callTools(IChatMessage iChatMessage) {
        try {
            UserMessage userMessage = new UserMessage(iChatMessage.getPrompt());
            final Prompt prompt = new Prompt(List.of(new SystemMessage(intentMessageResource), userMessage));
            var result = chatClient
                    .prompt(prompt)
                    .tools(aiToolService) // Auto-detects @Tool-annotated methods
                    .call()
                    .content(); // Get raw String response
            //  return parseStringToEvent(result); // Custom parsing logic
            return new Event("UNKNOWN", EventResponse.builder().content(result).build());
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize response into Event: " + e.getMessage(), e);
        }
    }

    public Event callTools_v2(IChatMessage iChatMessage) {
        try {
            UserMessage userMessage = new UserMessage(iChatMessage.getPrompt());
            final Prompt prompt = new Prompt(List.of(new SystemMessage(intentMessageResource), userMessage));
            ResponseEntity<ChatResponse, Event> result = chatClient
                    .prompt(prompt)
                    .tools(aiToolService) // Auto-detects @Tool-annotated methods
                    .call()
                    .responseEntity(Event.class); // Get raw String response
            //  return parseStringToEvent(result); // Custom parsing logic
            // return new Event("UNKNOWN",result);
            return result.entity();
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize response into Event: " + e.getMessage(), e);
        }
    }

    private Event parseStringToEvent(String result) throws JsonProcessingException {
        return objectMapper.readValue(result, Event.class);
    }

}