package com.yuce.chat.assistant.service;

import com.yuce.chat.assistant.model.Event;
import com.yuce.chat.assistant.model.IntentExtractionResult;
import org.springframework.stereotype.Service;

@Service
public interface ServiceDispatcher {
    Event getWeatherByCity(String city);

    Event getStockPriceBySymbol(String symbol);

    Event getWeather(IntentExtractionResult intent);

    Event getStockPrice(IntentExtractionResult intent);

    Event bookOperation(IntentExtractionResult intent);

    Event createRecipe(IntentExtractionResult intent);

    Event getDrugInformation(IntentExtractionResult intent);

    Event getChatBotUsers(IntentExtractionResult intent);
}
