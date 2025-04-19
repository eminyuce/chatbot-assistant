package com.yuce.chat.assistant.repo;

import com.yuce.chat.assistant.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Integer> {
    Book findByTitle(String title);
}
