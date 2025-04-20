package com.yuce.chat.assistant.tool;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yuce.chat.assistant.feign.StockClient;
import com.yuce.chat.assistant.feign.WeatherClient;
import com.yuce.chat.assistant.model.Event;
import com.yuce.chat.assistant.model.IntentResult;
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
