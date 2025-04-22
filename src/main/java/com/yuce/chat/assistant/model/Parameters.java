package com.yuce.chat.assistant.model;


import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Parameters {

    String city;

    String symbol;

    String title;
    String author;
    int year;

    String ingredients;
    String cuisine;
    String dietaryRestrictions;
}