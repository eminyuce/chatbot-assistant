package com.yuce.chat.assistant.service.impl;

import com.yuce.chat.assistant.model.*;
import com.yuce.chat.assistant.persistence.repository.UserPreferencesRepository;
import com.yuce.chat.assistant.service.IntentService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service("error-service")
@Slf4j
public class LLMErrorService implements IntentService {

    @Override
    public Event run(IntentExtractionResult intent) {
        return Event.builder().eventResponse(EventResponse.builder().content("LLM Model returns error.").build()).build();
    }


}