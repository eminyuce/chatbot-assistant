package com.yuce.chat.assistant.tool.impl;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.yuce.chat.assistant.model.Book;
import com.yuce.chat.assistant.model.Event;
import com.yuce.chat.assistant.model.EventResponse;
import com.yuce.chat.assistant.model.IntentResult;
import com.yuce.chat.assistant.service.BookService;
import com.yuce.chat.assistant.service.StockService;
import com.yuce.chat.assistant.service.WeatherService;
import com.yuce.chat.assistant.tool.AiToolService;
import com.yuce.chat.assistant.util.Constants;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service("external")
@AllArgsConstructor
public class ExternalAiToolService implements AiToolService {

    private final StockService stockService;

    private final WeatherService weatherService;

    private final BookService bookService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Tool(name = "getWeatherByCity", description = "Get the current weather for a given city")
    @Override
    public Event getWeatherByCity(@ToolParam(description = "city") String city) {
        if (!StringUtils.hasText(city)) {
            log.error("Invalid request: city is required.");
            return null;
        }

        return weatherService.getWeather(city);

    }

    @Tool(name = "getStockPriceBySymbol", description = "Get the current stock price for a given company symbol")
    @Override
    public Event getStockPriceBySymbol(@ToolParam(description = "symbol") String symbol) {
        if (!StringUtils.hasText(symbol)) {
            log.error("Invalid request: symbol is required.");
            return null;
        }

        return stockService.getStockPrice(symbol);

    }


    @Tool(name = "addBook", description = "Add A book to Database")
    @Override
    public Event addBook(@ToolParam(description = "title") String title,
                         @ToolParam(description = "author") String author,
                         @ToolParam(description = "year") int year) {

        var book = bookService.addBook(Book.builder().author(author).year(year).title(title).build());
        return Event.builder().type(Constants.BOOK).eventResponse(EventResponse.builder().content(book.toString()).build()).build();
    }

    @Override
    public Event getWeather(IntentResult intent) {
        return weatherService.getWeather(intent);
    }

    @Override
    public Event getStockPrice(IntentResult intent) {
        return stockService.getStockPrice(intent);
    }
}
