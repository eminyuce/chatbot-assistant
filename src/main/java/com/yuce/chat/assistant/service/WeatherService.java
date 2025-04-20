package com.yuce.chat.assistant.service;

import com.yuce.chat.assistant.feign.WeatherClient;
import com.yuce.chat.assistant.model.Event;
import com.yuce.chat.assistant.model.EventResponse;
import com.yuce.chat.assistant.model.IntentResult;
import com.yuce.chat.assistant.model.Parameters;
import com.yuce.chat.assistant.util.FormatTextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class WeatherService {

    @Autowired
    private WeatherClient weatherClient;
    public Event getWeather(String city) {
        return this.getWeather(IntentResult.builder().parameters(Parameters.builder().city(city).build()).build());
    }
    public Event getWeather(IntentResult intent) {
        var eventResponse = EventResponse.builder().build();
        if (intent.getParameters().getCity() != null) {
            try {
                var weather = weatherClient.getWeather(intent.getParameters().getCity(), "metric");
                return new Event("weather",eventResponse.setContent(FormatTextUtil.getInstance().formatWeatherResponse(weather.getBody())));
            } catch (Exception e) {
                return new Event("error",eventResponse.setContent( "Sorry, I couldn't fetch the weather for " + intent.getParameters().getCity() + ". Please try again."));
            }
        } else {
            return new Event("error", eventResponse.setContent( "Please specify a city for the weather query."));
        }
    }
}