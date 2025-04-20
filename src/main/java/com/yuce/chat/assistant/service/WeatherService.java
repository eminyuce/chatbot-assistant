package com.yuce.chat.assistant.service;

import com.yuce.chat.assistant.feign.WeatherClient;
import com.yuce.chat.assistant.model.Event;
import com.yuce.chat.assistant.model.IntentResult;
import com.yuce.chat.assistant.model.Parameters;
import com.yuce.chat.assistant.util.FormatTextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WeatherService {

    @Autowired
    private WeatherClient weatherClient;
    public Event getWeather(String city) {
        return this.getWeather(new IntentResult("",new Parameters(city,null)));
    }
    public Event getWeather(IntentResult intent) {
        if (intent.getParameters().getCity() != null) {
            try {
                var weather = weatherClient.getWeather(intent.getParameters().getCity(), "metric");
                return new Event("weather", FormatTextUtil.getInstance().formatWeatherResponse(weather.getBody()));
            } catch (Exception e) {
                return new Event("error", "Sorry, I couldn't fetch the weather for " + intent.getParameters().getCity() + ". Please try again.");
            }
        } else {
            return new Event("error", "Please specify a city for the weather query.");
        }
    }
}