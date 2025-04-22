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
public class IntentResult implements Serializable {
    String intent;
    @JsonProperty("sub_intent")
    String subIntent;
    Parameters parameters;
}

