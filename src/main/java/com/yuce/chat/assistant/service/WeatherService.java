package com.yuce.chat.assistant.service;

import org.springframework.stereotype.Service;

@Service
public class WeatherService {
    public String getWeather(String city) {
        return "It's sunny in " + city + " today!";
    }
}