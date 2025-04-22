package com.yuce.chat.assistant.service.impl;

import com.yuce.chat.assistant.model.Event;
import com.yuce.chat.assistant.model.EventResponse;
import com.yuce.chat.assistant.model.IntentResult;
import com.yuce.chat.assistant.persistence.entity.Book;
import com.yuce.chat.assistant.service.ServiceDispatcher;
import com.yuce.chat.assistant.service.BookService;
import com.yuce.chat.assistant.service.RecipeService;
import com.yuce.chat.assistant.util.Constants;
import com.yuce.chat.assistant.util.FormatTextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import static com.yuce.chat.assistant.util.Constants.*;

@Slf4j
@Service("static-services")
public class StaticServiceDispatcher implements ServiceDispatcher {

    @Qualifier("book-service-static")
    @Autowired
    private BookService bookService;

    @Autowired
    private RecipeService recipeService;

    @Tool(name = "getWeatherByCity", description = "Get the current weather for a given city")
    @Override
    public Event getWeatherByCity(String city) {
        return Event.builder().
                type(Constants.WEATHER).
                eventResponse(EventResponse.builder().content("CITY:" + city).build())
                .build();
    }

    @Tool(name = "getStockPriceBySymbol", description = "Get the current stock price for a given company symbol")
    @Override
    public Event getStockPriceBySymbol(String symbol) {
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

    @Override
    public Event bookOperation(IntentResult intent) {
        Book book = null;
        switch (intent.getSubIntent()) {
            case ADD_BOOK:
                book = bookService.addBook(getBook(intent));
            case DELETE_BOOK:
                book = bookService.findBookByName(intent.getParameters().getTitle());
                bookService.deleteByTitle(intent.getParameters().getTitle());
            case UPDATE_BOOK:
                book = bookService.updateBook(getBook(intent));
        }

        return Event.builder()
                .type(Constants.BOOK)
                .eventResponse(EventResponse.builder()
                        .content(FormatTextUtil.formatBookResponse(book, intent.getSubIntent()))
                        .build())
                .build();
    }

    private Book getBook(IntentResult intent) {
        String title = intent.getParameters().getTitle();
        String author = intent.getParameters().getAuthor();
        int year = intent.getParameters().getYear();
        Book book = bookService.addBook(Book.builder()
                .title(title)
                .author(author)
                .year(year)
                .build());
        return book;
    }


    @Tool(name = "addBook", description = "Add A book to Database")
    public Event addBook(@ToolParam(description = "title") String title,
                         @ToolParam(description = "author") String author,
                         @ToolParam(description = "year") int year) {
        Book book = Book.builder().author(author).year(year).title(title).build();
        bookService.addBook(book);
        return Event.builder().type(Constants.BOOK).eventResponse(EventResponse.builder().content("The Book is saved:" + book.toString()).build()).build();
    }

    @Override
    public Event createRecipe(IntentResult intent) {
        return recipeService.createRecipeStatic(intent);
    }
}

