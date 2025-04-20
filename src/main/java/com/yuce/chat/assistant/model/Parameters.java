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
}