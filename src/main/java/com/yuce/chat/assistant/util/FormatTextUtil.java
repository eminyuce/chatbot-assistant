package com.yuce.chat.assistant.util;

import com.yuce.chat.assistant.model.StockResponse;
import com.yuce.chat.assistant.model.WeatherResponse;

public class FormatTextUtil {
    private static FormatTextUtil instance = new FormatTextUtil();

    public static FormatTextUtil getInstance(){
        return instance;
    }
    public String formatWeatherResponse(WeatherResponse weather) {
        return String.format(
                "The weather in %s is %.1f°C with %d%% humidity.",
                weather.getName(),
                weather.getMain().getTemp(),
                weather.getMain().getHumidity()
        );
    }

    public String formatStockResponse(StockResponse stock) {
        return String.format(
                "The stock price of %s is $%s.",
                stock.getGlobalQuote().getSymbol(),
                stock.getGlobalQuote().getPrice()
        );
    }
}
