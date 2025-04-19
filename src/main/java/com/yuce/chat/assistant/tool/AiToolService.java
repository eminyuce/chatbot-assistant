package com.yuce.chat.assistant.tool;

import com.yuce.chat.assistant.feign.StockClient;
import com.yuce.chat.assistant.feign.WeatherClient;
import com.yuce.chat.assistant.model.StockResponse;
import com.yuce.chat.assistant.model.WeatherResponse;
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

    private final WeatherClient weatherClient;

    private final StockClient stockClient;

    @Tool(name = "getWeatherByCity", description = "Get the current weather for a given city")
    public WeatherResponse getWeatherByCity(@ToolParam(description = "city") String city)
    {
        if (!StringUtils.hasText(city)) {
            log.error("Invalid request: city is required.");
            return null;
        }
        var responseEntity = weatherClient.getWeather(city, "metrics");
        if(responseEntity.getStatusCode().is2xxSuccessful()){
            return responseEntity.getBody();
        }else{
            log.error("Invalid Request. Status Code:"+responseEntity.getStatusCode());
            return null;
        }
    }

    @Tool(name = "getStockPriceBySymbol", description = "Get the current stock price for a given company symbol")
    public StockResponse getStockPriceBySymbol(@ToolParam(description = "symbol") String symbol)
    {
        if (!StringUtils.hasText(symbol)) {
            log.error("Invalid request: symbol is required.");
            return null;
        }
        var responseEntity = stockClient.getStockPrice(symbol);
        if(responseEntity.getStatusCode().is2xxSuccessful()){
            return responseEntity.getBody();
        }else{
            log.error("Invalid Request. Status Code:"+responseEntity.getStatusCode());
            return null;
        }
    }
}
