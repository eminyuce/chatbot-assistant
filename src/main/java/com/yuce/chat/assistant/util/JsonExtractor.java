package com.yuce.chat.assistant.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonExtractor {

    public static String extractJsonString(String inputText) {
        if (isItValidJson(inputText)) {
            return inputText;
        }
        // Regex pattern to match text between ```json and ```
        String regex = "```json(.*?)```";
        var pattern = Pattern.compile(regex, Pattern.DOTALL);
        var matcher = pattern.matcher(inputText);

        if (matcher.find()) {
            // Return the extracted JSON without the surrounding markdown
            return matcher.group(1).trim();
        }
        return null;
    }

    public static boolean isItValidJson(String str) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // Try to parse the string as a JSON object
            objectMapper.readTree(str);
            return true; // If no exception occurs, it's a valid JSON
        } catch (IOException e) {
            return false; // If parsing fails, it's not a valid JSON
        }
    }

    // Method to extract JSON string from any input string
    public static String extractJsonString_v2(String inputText) {
        // Regular expression to match a JSON string enclosed in curly braces
        String jsonPattern = "\\{.*?\\}";  // Match the shortest possible JSON object

        // Compile the pattern and create the matcher
        Pattern pattern = Pattern.compile(jsonPattern, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(inputText);

        // Check if we find a match and return the matched JSON string
        if (matcher.find()) {
            return matcher.group();  // Return the matched JSON string
        }

        return null;  // Return null if no JSON string is found
    }
}
