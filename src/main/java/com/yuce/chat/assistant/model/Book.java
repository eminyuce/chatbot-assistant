package com.yuce.chat.assistant.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Book {
    @Id
    private int id;
    private String title;
    private String author;
    int year;
}
