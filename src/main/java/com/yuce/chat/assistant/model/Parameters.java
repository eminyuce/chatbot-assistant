package com.yuce.chat.assistant.model;


import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Parameters implements Serializable {

    String city;

    String symbol;

    String title;
    String author;
    int year;

    String ingredients;
    String cuisine;
    String dietaryRestrictions;
}