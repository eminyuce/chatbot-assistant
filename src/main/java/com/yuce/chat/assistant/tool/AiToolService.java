package com.yuce.chat.assistant.tool;

import com.yuce.chat.assistant.model.Event;
import com.yuce.chat.assistant.model.IntentResult;
import org.springframework.stereotype.Service;

@Service
public interface AiToolService {
    Event getWeatherByCity(String city);

    Event getStockPriceBySymbol(String symbol);

    Event getWeather(IntentResult intent);

    Event getStockPrice(IntentResult intent);

    Event addBook(String title,
                  String author,
                  int year);
}
