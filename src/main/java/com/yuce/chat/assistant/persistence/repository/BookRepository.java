package com.yuce.chat.assistant.persistence.repository;

import com.yuce.chat.assistant.persistence.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    Book findByTitle(String title);

    void deleteByTitle(String title);

    List<Book> findByAuthorContainingIgnoreCase(String author);

    List<Book> findByYear(Integer year);
}
