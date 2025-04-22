package com.yuce.chat.assistant.util;

import com.yuce.chat.assistant.model.StockResponse;
import com.yuce.chat.assistant.model.WeatherResponse;
import com.yuce.chat.assistant.persistence.entity.Book;

public class FormatTextUtil {
    private static FormatTextUtil instance = new FormatTextUtil();

    public static FormatTextUtil getInstance() {
        return instance;
    }


    public static String formatBookResponse(Book book, String subIntent) {
        if (book == null) {
            return "No book found or affected.";
        }

        switch (subIntent) {
            case Constants.ADD_BOOK:
                return String.format("Book added successfully: '%s' by %s (%d)",
                        book.getTitle(), book.getAuthor(), book.getYear());

            case Constants.DELETE_BOOK:
                return String.format("Book deleted successfully: '%s'", book.getTitle());

            case Constants.UPDATE_BOOK:
                return String.format("Book updated successfully: '%s' by %s (%d)",
                        book.getTitle(), book.getAuthor(), book.getYear());

            default:
                return "Unknown operation performed on the book.";
        }
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
