package com.yuce.chat.assistant.service.impl;

import com.yuce.chat.assistant.model.Event;
import com.yuce.chat.assistant.model.EventResponse;
import com.yuce.chat.assistant.model.IntentExtractionResult;
import com.yuce.chat.assistant.persistence.entity.Book;
import com.yuce.chat.assistant.persistence.repository.BookRepository;
import com.yuce.chat.assistant.service.BookService;
import com.yuce.chat.assistant.service.IntentService;
import com.yuce.chat.assistant.util.Constants;
import com.yuce.chat.assistant.util.FormatTextUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.yuce.chat.assistant.util.Constants.*;

@Service("book-service")
@AllArgsConstructor
@Slf4j
public class BookServiceImpl implements BookService, IntentService {

    private final BookRepository bookRepository;

    @Override
    public Book addBook(Book book) {
        bookRepository.save(book);
        return book;
    }

    @Override
    public Book findBookByTitle(String title) {
        return bookRepository.findByTitle(title);
    }

    @Override
    public List<Book> findBookByAuthor(IntentExtractionResult intent) {
        if (intent.getParameters().getAuthor() != null) {
            return bookRepository.findByAuthorContainingIgnoreCase(intent.getParameters().getAuthor());
        } else if (intent.getParameters().getTitle() != null) {
            return List.of(bookRepository.findByTitle(intent.getParameters().getTitle()));
        } else if (intent.getParameters().getYear() != null) {
            return bookRepository.findByYear(intent.getParameters().getYear());
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public List<Book> listBooks() {
        return bookRepository.findAll();
    }

    @Override
    public void deleteByTitle(String title) {
        bookRepository.deleteByTitle(title);
    }

    @Override
    public Book updateBook(Book book) {
        var savedBook = this.findBookByTitle(book.getTitle());
        savedBook.setYear(book.getYear());
        savedBook.setAuthor(book.getAuthor());
        this.addBook(savedBook);
        return savedBook;
    }

    @Override
    public Event run(IntentExtractionResult intent) {
        List<Book> books = new ArrayList<>();
        switch (intent.getSubIntent()) {
            case ADD_BOOK:
                books.add(addBook(getBook(intent)));
                break;
            case DELETE_BOOK:
                books.add(findBookByTitle(intent.getParameters().getTitle()));
                deleteByTitle(intent.getParameters().getTitle());
                break;
            case UPDATE_BOOK:
                books.add(updateBook(getBook(intent)));
                break;
            case FIND_BOOK:
                books.addAll(findBookByAuthor(intent));
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
        return book.setAuthor(author).setYear(year).setTitle(title);
    }
}