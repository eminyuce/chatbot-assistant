package com.yuce.chat.assistant.service;

import com.yuce.chat.assistant.model.Event;
import com.yuce.chat.assistant.model.IntentResult;
import org.springframework.stereotype.Service;

@Service
public interface ServiceDispatcher {
    Event getWeatherByCity(String city);

    Event getStockPriceBySymbol(String symbol);

    Event getWeather(IntentResult intent);

    Event getStockPrice(IntentResult intent);

    Event bookOperation(IntentResult intent);

    Event createRecipe(IntentResult intent);

    Event getDrugInformation(IntentResult intent);

    Event getChatBotUsers(IntentResult intent);
}
