package com.yuce.chat.assistant.service.impl;

import com.yuce.chat.assistant.config.ObservabilityConfig;
import com.yuce.chat.assistant.model.Event;
import com.yuce.chat.assistant.model.IChatMessage;
import com.yuce.chat.assistant.model.IntentExtractionResult;
import com.yuce.chat.assistant.service.IntentService;
import io.micrometer.core.instrument.Timer;
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
    @Autowired(required = false)
    private ObservabilityConfig.ChatMetrics chatMetrics;

    public Event getResponseStream(IChatMessage iChatMessage) {
        Timer.Sample sample = chatMetrics != null ? chatMetrics.startChatLatency() : null;
        String intentName = null;
        try {
            IntentExtractionResult intent = intentMatchingService.extractIntention(iChatMessage);
            intentName = intent.getIntent();
            var intentService = applicationContext.getBean(intent.getIntent() + "-service", IntentService.class);
            Event event = intentService.run(intent);
            if (chatMetrics != null) {
                chatMetrics.recordChatRequest(intentName, "success");
                if (sample != null) chatMetrics.recordChatLatency(sample, intentName);
            }
            return event;
        } catch (Exception e) {
            if (chatMetrics != null) {
                chatMetrics.recordChatRequest(intentName != null ? intentName : "unknown", "error");
                chatMetrics.recordError("ChatService", e.getClass().getSimpleName());
                if (sample != null) chatMetrics.recordChatLatency(sample, intentName != null ? intentName : "unknown");
            }
            throw e;
        }
    }
}