package com.yuce.chat.assistant.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;
import java.util.Arrays;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class IntentExtractionResult implements Serializable {
    String intent;
    @JsonProperty("sub_intent")
    String subIntent;
    Parameters parameters;
    @JsonIgnore
    IChatMessage iChatMessage;

    public IntentExtractionResult(String intent, String subIntent, Parameters parameters) {
        this.intent = intent;
        this.subIntent = subIntent;
        this.parameters = parameters;
    }

    public static Builder builder() {
        return new Builder();
    }

    public boolean hasAccessRole(String... roles) {
        if (iChatMessage.getChatBotRoles() == null || roles == null) {
            return false;
        }
        // Check if any role in the provided roles array is contained in the chatBotRoles list
        return iChatMessage.getChatBotRoles().stream().anyMatch(role ->
                Arrays.asList(roles).contains(role));
    }

    // Builder pattern implementation
    public static class Builder {
        private String intent;
        private String subIntent;
        private Parameters parameters;

        public Builder intent(String intent) {
            this.intent = intent;
            return this;
        }

        public Builder subIntent(String subIntent) {
            this.subIntent = subIntent;
            return this;
        }

        public Builder parameters(Parameters parameters) {
            this.parameters = parameters;
            return this;
        }

        public IntentExtractionResult build() {
            IntentExtractionResult result = new IntentExtractionResult(intent, subIntent, parameters);
            return result;
        }
    }
}

