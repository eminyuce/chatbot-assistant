package com.yuce.chat.assistant.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class JsonExtractorTest {

    @Test
    public void testExtractJsonString() {
        // Input string with JSON data inside markdown
        String inputText = "Based on the user prompt, I can determine that the intent is indeed \"drug\". Since there's no sub-intent mentioned in the prompt, I'll set it to \"no_sub_intent\".\n\n"
                + "As for the parameters, I see that the user has provided a specific request related to a drug. Therefore, I'll extract the relevant parameter from the prompt, which is the \"drug_name\" parameter.\n\n"
                + "Here's the JSON response:\n\n"
                + "```json\n"
                + "{\n"
                + "  \"intent\": \"drug\",\n"
                + "  \"sub_intent\": \"no_sub_intent\",\n"
                + "  \"parameters\": {\n"
                + "    \"city\": null,\n"
                + "    \"symbol\": null,\n"
                + "    \"title\": null,\n"
                + "    \"author\": null,\n"
                + "    \"year\": null,\n"
                + "    \"food_name\": null,\n"
                + "    \"drug_name\": \"drug\"\n"
                + "  }\n"
                + "}\n"
                + "```\n"
                + "Note that I've set the \"drug_name\" parameter to the string \"drug\", as it's the only relevant information provided in the prompt.";

        // Expected output
        String expectedJson = "{\n"
                + "  \"intent\": \"drug\",\n"
                + "  \"sub_intent\": \"no_sub_intent\",\n"
                + "  \"parameters\": {\n"
                + "    \"city\": null,\n"
                + "    \"symbol\": null,\n"
                + "    \"title\": null,\n"
                + "    \"author\": null,\n"
                + "    \"year\": null,\n"
                + "    \"food_name\": null,\n"
                + "    \"drug_name\": \"drug\"\n"
                + "  }\n"
                + "}";

        // Call the method
        String extractedJson = JsonExtractor.extractJsonString(inputText);

        // Assert that the extracted JSON is equal to the expected JSON
        assertEquals(expectedJson, extractedJson, "The extracted JSON should match the expected JSON.");
    }

    @Test
    public void testExtractJsonStringWithNoJson() {
        // Input string without JSON markdown
        String inputText = "This is some text without any JSON markup.";

        // Call the method
        String extractedJson = JsonExtractor.extractJsonString(inputText);

        // Assert that the extracted JSON is null (since no JSON was found)
        assertNull(extractedJson, "The extracted JSON should be null when no JSON is present in the input.");
    }

    @Test
    public void testExtractJsonStringWithEmptyJson() {
        // Input string with empty JSON markdown
        String inputText = "Here is an empty JSON response:\n\n```json\n\n```";

        // Call the method
        String extractedJson = JsonExtractor.extractJsonString(inputText);

        // Assert that the extracted JSON is empty
        assertEquals("", extractedJson, "The extracted JSON should be empty when no content is inside the JSON markdown.");
    }
}
