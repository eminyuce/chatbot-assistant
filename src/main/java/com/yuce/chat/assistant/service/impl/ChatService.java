package com.yuce.chat.assistant.service.impl;

import com.yuce.chat.assistant.model.Event;
import com.yuce.chat.assistant.model.IChatMessage;
import com.yuce.chat.assistant.model.IntentExtractionResult;
import com.yuce.chat.assistant.service.IntentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class ChatService {
    @Autowired
    private IntentMatchingService intentMatchingService;
    @Autowired
    private ApplicationContext applicationContext;

    public Event getResponseStream(IChatMessage iChatMessage) {
        IntentExtractionResult intent = intentMatchingService.extractIntention(iChatMessage);
        var intentService = applicationContext.getBean(intent.getIntent() + "-service", IntentService.class);
        if (intentService == null) {
            throw new IllegalArgumentException("No Bean is registered to spring context for " + intent.getIntent());
        } else {
            return intentService.run(intent);
        }
    }

}