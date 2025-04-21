package com.yuce.chat.assistant.service;

import com.yuce.chat.assistant.persistence.entity.Book;
import com.yuce.chat.assistant.repo.BookRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class BookService {

    private final BookRepository bookRepository;

    @Tool(description = "Add a new book to the store")
    public Book addBook(Book book) {
        bookRepository.save(book);
        return book;
    }

    @Tool(description = "Find a book by its title")
    public Book findBookByName(String title) {
        return bookRepository.findByTitle(title);
    }

    @Tool(description = "List all books in the store")
    public List<Book> listBooks() {
        return bookRepository.findAll();
    }
}