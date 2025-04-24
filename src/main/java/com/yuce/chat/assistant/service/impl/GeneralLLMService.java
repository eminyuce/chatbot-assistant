package com.yuce.chat.assistant.service.impl;

import com.yuce.chat.assistant.model.Event;
import com.yuce.chat.assistant.model.EventResponse;
import com.yuce.chat.assistant.model.IChatMessage;
import com.yuce.chat.assistant.model.IntentExtractionResult;
import com.yuce.chat.assistant.service.IntentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.yuce.chat.assistant.util.Constants.CHAT;

@Service("general-service")
@Slf4j
public class GeneralLLMService implements IntentService {

    @Autowired
    private ChatModel chatModel;

    @Override
    public Event run(IntentExtractionResult intent) {
        return getPromptCall(intent.getIChatMessage());
    }

    private Event getPromptCall(IChatMessage iChatMessage) {
        try {
            String response = chatModel.call(iChatMessage.getPrompt());
            return new Event(CHAT, EventResponse.builder().content(response).build());
        } catch (Exception e) {
            return new Event(CHAT, EventResponse.builder().content(e.getMessage()).build());
        }
    }
}