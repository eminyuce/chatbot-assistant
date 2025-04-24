package com.yuce.chat.assistant.service.impl;

import com.yuce.chat.assistant.feign.WeatherClient;
import com.yuce.chat.assistant.model.Event;
import com.yuce.chat.assistant.model.EventResponse;
import com.yuce.chat.assistant.model.IntentExtractionResult;
import com.yuce.chat.assistant.model.Parameters;
import com.yuce.chat.assistant.service.IntentService;
import com.yuce.chat.assistant.util.Constants;
import com.yuce.chat.assistant.util.FormatTextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("weather-service")
@Slf4j
public class WeatherServiceImpl implements com.yuce.chat.assistant.service.WeatherService, IntentService {

    @Autowired
    private WeatherClient weatherClient;

    @Override
    public Event getWeather(String city) {
        return this.getWeather(IntentExtractionResult.builder().parameters(Parameters.builder().city(city).build()).build());
    }

    @Override
    public Event getWeather(IntentExtractionResult intent) {
        // Check for null intent, parameters, or city
        if (intent == null || intent.getParameters() == null || intent.getParameters().getCity() == null) {
            return new Event(Constants.ERROR, new EventResponse("Please specify a city for the weather query."));
        }

        String city = intent.getParameters().getCity();
        try {
            var weatherResponse = weatherClient.getWeather(city, "metric");
            if (weatherResponse == null || weatherResponse.getBody() == null) {
                return new Event(Constants.ERROR, new EventResponse("No weather data found for city: " + city));
            }
            return new Event(Constants.WEATHER, new EventResponse(FormatTextUtil.getInstance().formatWeatherResponse(weatherResponse.getBody())));
        } catch (RuntimeException e) {
            return new Event(Constants.ERROR, new EventResponse("Failed to fetch weather for " + city + ": " + e.getMessage()));
        }
    }

    @Override
    public Event run(IntentExtractionResult intent) {
        return this.getWeather(intent);
    }
}