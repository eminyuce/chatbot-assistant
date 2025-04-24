package com.yuce.chat.assistant.service;

import com.yuce.chat.assistant.model.IntentExtractionResult;
import com.yuce.chat.assistant.persistence.entity.Book;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface BookService {
    Book addBook(Book book);

    Book findBookByTitle(String title);

    List<Book> findBookByAuthor(IntentExtractionResult intent);

    List<Book> listBooks();

    void deleteByTitle(String title);

    Book updateBook(Book book);
}
