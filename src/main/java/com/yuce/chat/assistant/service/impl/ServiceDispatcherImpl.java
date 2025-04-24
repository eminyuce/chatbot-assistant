package com.yuce.chat.assistant.service.impl;


import com.yuce.chat.assistant.model.Event;
import com.yuce.chat.assistant.model.EventResponse;
import com.yuce.chat.assistant.model.IntentExtractionResult;
import com.yuce.chat.assistant.persistence.entity.Book;
import com.yuce.chat.assistant.service.*;
import com.yuce.chat.assistant.util.Constants;
import com.yuce.chat.assistant.util.FormatTextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static com.yuce.chat.assistant.util.Constants.*;

@Slf4j
@Service
public class ServiceDispatcherImpl implements ServiceDispatcher {

    @Autowired
    private StockService stockService;
    @Autowired
    private WeatherService weatherService;

    @Autowired
    private DrugService drugService;

    @Autowired
    private BookService bookService;
    @Autowired
    private RecipeService recipeService;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Override
    public Event getWeatherByCity(String city) {
        if (!StringUtils.hasText(city)) {
            log.error("Invalid request: city is required.");
            return null;
        }

        return weatherService.getWeather(city);

    }

    @Override
    public Event getStockPriceBySymbol(String symbol) {
        if (!StringUtils.hasText(symbol)) {
            log.error("Invalid request: symbol is required.");
            return null;
        }

        return stockService.getStockPrice(symbol);

    }


    public Event addBook(String title,
                         String author,
                         int year) {
        var book = new Book();
        book = bookService.addBook(book.setAuthor(author).setYear(year).setTitle(title));
        return Event.builder().type(Constants.BOOK).eventResponse(EventResponse.builder().content(book.toString()).build()).build();
    }

    @Override
    public Event createRecipe(IntentExtractionResult intent) {
        return recipeService.createRecipe(intent);
    }

    @Override
    public Event getDrugInformation(IntentExtractionResult intent) {
        return drugService.getDrugInformation(intent);
    }

    @Override
    public Event getChatBotUsers(IntentExtractionResult intent) {
        return userDetailsService.getChatBotUsers(intent);
    }

    @Override
    public Event getWeather(IntentExtractionResult intent) {
        return weatherService.getWeather(intent);
    }

    @Override
    public Event getStockPrice(IntentExtractionResult intent) {
        return stockService.getStockPrice(intent);
    }

    @Override
    public Event bookOperation(IntentExtractionResult intent) {
        List<Book> books = new ArrayList<>();
        switch (intent.getSubIntent()) {
            case ADD_BOOK:
                books.add(bookService.addBook(getBook(intent)));
                break;
            case DELETE_BOOK:
                books.add(bookService.findBookByTitle(intent.getParameters().getTitle()));
                bookService.deleteByTitle(intent.getParameters().getTitle());
                break;
            case UPDATE_BOOK:
                books.add(bookService.updateBook(getBook(intent)));
                break;
            case FIND_BOOK:
                books.addAll(bookService.findBookByAuthor(intent));
                break;
        }

        return Event.builder()
                .type(Constants.BOOK)
                .eventResponse(EventResponse.builder()
                        .content(FormatTextUtil.formatBookResponse(books, intent.getSubIntent()))
                        .build())
                .build();
    }

    private Book getBook(IntentExtractionResult intent) {
        String title = intent.getParameters().getTitle();
        String author = intent.getParameters().getAuthor();
        int year = intent.getParameters().getYear();
        var book = new Book();
        book = bookService.addBook(book.setAuthor(author).setYear(year).setTitle(title));
        return book;
    }
}
