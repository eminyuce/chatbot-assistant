package com.yuce.chat.assistant.tool;

import com.yuce.chat.assistant.feign.StockClient;
import com.yuce.chat.assistant.feign.WeatherClient;
import com.yuce.chat.assistant.model.Event;
import com.yuce.chat.assistant.model.StockResponse;
import com.yuce.chat.assistant.model.WeatherResponse;
import com.yuce.chat.assistant.service.StockService;
import com.yuce.chat.assistant.service.WeatherService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@AllArgsConstructor
public class AiToolService {

    private final StockService stockService;

    private final WeatherService weatherService;

    @Tool(name = "getWeatherByCity", description = "Get the current weather for a given city")
    public Event getWeatherByCity(@ToolParam(description = "city") String city)
    {
        if (!StringUtils.hasText(city)) {
            log.error("Invalid request: city is required.");
            return null;
        }
        return weatherService.getWeather(city);
    }

    @Tool(name = "getStockPriceBySymbol", description = "Get the current stock price for a given company symbol")
    public Event getStockPriceBySymbol(@ToolParam(description = "symbol") String symbol)
    {
        if (!StringUtils.hasText(symbol)) {
            log.error("Invalid request: symbol is required.");
            return null;
        }
        return stockService.getStockPrice(symbol);
    }
}
