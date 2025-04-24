package com.yuce.chat.assistant.service.impl;

import com.yuce.chat.assistant.model.IntentExtractionResult;
import com.yuce.chat.assistant.persistence.entity.Book;
import com.yuce.chat.assistant.persistence.repository.BookRepository;
import com.yuce.chat.assistant.service.BookService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service("book-service")
@AllArgsConstructor
@Slf4j
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    @Override
    @Tool(description = "Add a new book to the store")
    public Book addBook(Book book) {
        bookRepository.save(book);
        return book;
    }

    @Override
    @Tool(description = "Find a book by its title")
    public Book findBookByTitle(String title) {
        return bookRepository.findByTitle(title);
    }

    @Override
    public List<Book> findBookByAuthor(IntentExtractionResult intent) {
        if (intent.getParameters().getAuthor() != null) {
            return bookRepository.findBookByAuthor(intent.getParameters().getAuthor());
        } else if (intent.getParameters().getTitle() != null) {
            return List.of(bookRepository.findByTitle(intent.getParameters().getTitle()));
        } else if (intent.getParameters().getYear() != null) {
            return bookRepository.findByYear(intent.getParameters().getYear());
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    @Tool(description = "List all books in the store")
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
}