package com.yuce.chat.assistant.model;

import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public  class IntentResult {
        String intent;
        Parameters parameters;
}

