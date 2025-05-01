package com.yuce.chat.assistant.util;

import com.fasterxml.jackson.databind.ObjectMapper;

public class BeanOutputParser<T> {

    private final Class<T> outputClass;
    private final ObjectMapper objectMapper;

    public BeanOutputParser(Class<T> outputClass, ObjectMapper objectMapper) {
        this.outputClass = outputClass;
        this.objectMapper = objectMapper;
    }

    /**
     * Generates basic instructions for the LLM about the expected JSON format.
     * You can customize this to be more detailed or include JSON schema.
     */
    public String getFormat() {
        try {
            T emptyInstance = outputClass.getDeclaredConstructor().newInstance();
            String exampleJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(emptyInstance);
            return exampleJson;
        } catch (Exception e) {
            return "Please respond in valid JSON format matching this class: " + outputClass.getSimpleName();
        }
    }

    /**
     * Parses a JSON string into an instance of T.
     *
     * @param json JSON string from the LLM
     * @return Parsed object
     * @throws Exception if parsing fails
     */
    public T parse(String json) throws Exception {
        return objectMapper.readValue(json, outputClass);
    }
}
