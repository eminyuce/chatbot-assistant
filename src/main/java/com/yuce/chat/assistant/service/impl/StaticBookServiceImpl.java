package com.yuce.chat.assistant.service.impl;

import com.yuce.chat.assistant.persistence.entity.Book;
import com.yuce.chat.assistant.service.BookService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service("book-service-static")
@AllArgsConstructor
@Slf4j
public class StaticBookServiceImpl implements BookService {

    private final List<Book> books = new CopyOnWriteArrayList<>();

    @Override
    public Book addBook(Book book) {
        books.add(book);
        log.info("Book added: {}", book);
        return book;
    }

    @Override
    public Book findBookByName(String title) {
        return books.stream()
                .filter(book -> book.getTitle().equalsIgnoreCase(title))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Book> listBooks() {
        return new ArrayList<>(books);
    }

    @Override
    public void deleteByTitle(String title) {
        books.removeIf(book -> book.getTitle().equalsIgnoreCase(title));
        log.info("Book with title '{}' deleted", title);
    }

    @Override
    public Book updateBook(Book book) {
        deleteByTitle(book.getTitle());
        books.add(book);
        log.info("Book updated: {}", book);
        return book;
    }
}
