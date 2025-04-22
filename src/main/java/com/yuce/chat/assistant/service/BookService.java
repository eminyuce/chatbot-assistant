package com.yuce.chat.assistant.service;

import com.yuce.chat.assistant.persistence.entity.Book;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface BookService {
    @Tool(description = "Add a new book to the store")
    Book addBook(Book book);

    @Tool(description = "Find a book by its title")
    Book findBookByName(String title);

    @Tool(description = "List all books in the store")
    List<Book> listBooks();

    void deleteByTitle(String title);

    Book updateBook(Book book);
}
