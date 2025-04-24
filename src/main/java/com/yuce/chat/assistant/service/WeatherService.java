package com.yuce.chat.assistant.service;

import com.yuce.chat.assistant.model.Event;
import com.yuce.chat.assistant.model.IntentExtractionResult;
import org.springframework.stereotype.Service;

@Service
public interface WeatherService {
    Event getWeather(String city);

    Event getWeather(IntentExtractionResult intent);
}
