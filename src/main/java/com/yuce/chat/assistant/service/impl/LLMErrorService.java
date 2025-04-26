package com.yuce.chat.assistant.service.impl;

import com.yuce.chat.assistant.model.Event;
import com.yuce.chat.assistant.model.EventResponse;
import com.yuce.chat.assistant.model.IntentExtractionResult;
import com.yuce.chat.assistant.service.IntentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service("error-service")
@Slf4j
public class LLMErrorService implements IntentService {

    @Override
    public Event run(IntentExtractionResult intent) {
        return Event.builder().eventResponse(EventResponse.builder().content("LLM Model returns error.").build()).build();
    }


}