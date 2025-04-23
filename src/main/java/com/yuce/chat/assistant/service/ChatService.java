package com.yuce.chat.assistant.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yuce.chat.assistant.model.*;
import com.yuce.chat.assistant.util.JsonExtractor;
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
    private IntentMatchingService intentMatchingService;

    @Autowired
    @Qualifier("static-services")
    private ServiceDispatcher serviceDispatcher;
    @Autowired
    private ObjectMapper objectMapper;

    public Event getResponseStream(IChatMessage iChatMessage) {
        IntentExtractionResult intent = intentMatchingService.determineIntentAndExtract(iChatMessage.getPrompt());
        switch (intent.getIntent()) {
            case WEATHER:
                return serviceDispatcher.getWeather(intent);
            case DRUG:
                return serviceDispatcher.getDrugInformation(intent);
            case CHAT_BOT_USERS:
                return serviceDispatcher.getChatBotUsers(intent);
            case STOCK_PRICE:
                return serviceDispatcher.getStockPrice(intent);
            case RECIPE:
                return serviceDispatcher.createRecipe(intent);
            case BOOK:
                return serviceDispatcher.bookOperation(intent);
            case ERROR:
                return Event.builder().eventResponse(EventResponse.builder().content("LLM Model returns error.").build()).build();
            default:
                return getPromptCall(iChatMessage);
        }
    }

    private Event getPromptCall(IChatMessage iChatMessage) {
        try {
            String response = chatModel.call(iChatMessage.getPrompt());
            return new Event(CHAT, EventResponse.builder().content(response).build());
        } catch (Exception e) {
            return new Event(CHAT, EventResponse.builder().content(e.getMessage()).build());
        }
    }
    private IntentExtractionResult detectIntent(IChatMessage iChatMessage) {
        try {
            Prompt intentPrompt = new Prompt(List.of(
                    new SystemMessage(intentMessageResource),
                    new UserMessage(iChatMessage.getPrompt())
            ));
            String rawJsonResponse = chatModel.call(intentPrompt).getResult().getOutput().getText();
            String jsonResponse = JsonExtractor.extractJson(rawJsonResponse);

            var result =  objectMapper.readValue(jsonResponse, IntentExtractionResult.class);
            result.setIChatMessage(iChatMessage);
            return result;
        } catch (Exception e) {
            // Fallback to general query if intent detection fails
            log.error("Detecting intention error", e);
            return new IntentExtractionResult("error", "", new Parameters());
        }
    }
    public Event callTools(IChatMessage iChatMessage) {
        try {
            UserMessage userMessage = new UserMessage(iChatMessage.getPrompt());
            final Prompt prompt = new Prompt(List.of(new SystemMessage(this.intentMessageResource), userMessage));
            ResponseEntity<ChatResponse, Event> result = chatClient
                    .prompt(prompt)
                    .tools(serviceDispatcher) // Auto-detects @Tool-annotated methods
                    .call()
                    .responseEntity(Event.class); // Get raw String response
            //  return parseStringToEvent(result); // Custom parsing logic
            // return new Event("UNKNOWN",result);
            return result.entity();
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize response into Event: " + e.getMessage(), e);
        }
    }

}