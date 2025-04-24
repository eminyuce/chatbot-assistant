package com.yuce.chat.assistant.service;

import com.yuce.chat.assistant.model.Event;
import com.yuce.chat.assistant.model.IntentExtractionResult;

public interface IntentService {
    Event run(IntentExtractionResult intent);
}
