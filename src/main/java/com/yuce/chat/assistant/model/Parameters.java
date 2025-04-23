package com.yuce.chat.assistant.model;


import com.fasterxml.jackson.annotation.JsonProperty;
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
    Integer year;

    @JsonProperty("food_name")
    String foodName;

    @JsonProperty("drug_name")
    String drugName;
}