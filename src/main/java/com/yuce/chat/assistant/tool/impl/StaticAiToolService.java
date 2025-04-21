package com.yuce.chat.assistant.tool.impl;

import com.yuce.chat.assistant.model.Event;
import com.yuce.chat.assistant.model.EventResponse;
import com.yuce.chat.assistant.model.IntentResult;
import com.yuce.chat.assistant.persistence.entity.Book;
import com.yuce.chat.assistant.tool.AiToolService;
import com.yuce.chat.assistant.util.Constants;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service("static")
@AllArgsConstructor
public class StaticAiToolService implements AiToolService {

    private final Map<String, Book> bookStorage = new HashMap<>();

    @Tool(name = "getWeatherByCity", description = "Get the current weather for a given city")
    @Override
    public Event getWeatherByCity(@ToolParam(description = "city") String city) {
        return Event.builder().
                type(Constants.WEATHER).
                eventResponse(EventResponse.builder().content("CITY:" + city).build())
                .build();
    }

    @Tool(name = "getStockPriceBySymbol", description = "Get the current stock price for a given company symbol")
    @Override
    public Event getStockPriceBySymbol(@ToolParam(description = "symbol") String symbol) {
        return Event.builder().type(Constants.STOCK).eventResponse(EventResponse.builder().content("symbol" + symbol).build()).build();
    }

    @Override
    public Event getWeather(IntentResult intent) {
        return Event.builder().type(Constants.WEATHER).eventResponse(EventResponse.builder().content("city:" + intent.getParameters().getCity()).build()).build();
    }

    @Override
    public Event getStockPrice(IntentResult intent) {
        return Event.builder().type(Constants.STOCK).eventResponse(EventResponse.builder().content("symbol:" + intent.getParameters().getSymbol()).build()).build();
    }

    @Tool(name = "addBook", description = "Add A book to Database")
    @Override
    public Event addBook(@ToolParam(description = "title") String title,
                         @ToolParam(description = "author") String author,
                         @ToolParam(description = "year") int year) {
        Book book = Book.builder().author(author).year(year).title(title).build();
        bookStorage.put(title, book);
        return Event.builder().type(Constants.BOOK).eventResponse(EventResponse.builder().content("book:" + book.toString()).build()).build();
    }
}

