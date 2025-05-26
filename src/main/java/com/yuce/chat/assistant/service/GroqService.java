package com.yuce.chat.assistant.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yuce.chat.assistant.config.GroqConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

@Service
public class GroqService {

    private static final String GROQ_API_URL = "https://api.groq.com/openai/v1/chat/completions";
    private static final Path CSV_PATH = Path.of("C:/Users/YUCE/Desktop/Education/urunler_groq.csv");

    @Autowired
    private GroqConfig groqConfig;

    private final ObjectMapper mapper = new ObjectMapper();

    public void generateDescriptions() throws IOException, InterruptedException {
        List<String> lines = Files.readAllLines(CSV_PATH);
        if (lines.isEmpty()) return;

        List<String> outputLines = new ArrayList<>();
        outputLines.add(lines.get(0)); // header

        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            String[] cols = line.split(",", -1);

            String stockCode = cols.length > 0 ? cols[0].trim() : "";
            String productName = cols.length > 1 ? cols[1].trim().replaceAll("\"", "") : "";
            String bullets = cols.length > 2 ? cols[2].trim() : "";

            try {
                if (!productName.isBlank()) {
                    String prompt = buildPrompt(productName);
                    String aiResponse = getGroqResponse(prompt);

                    System.out.printf("%d) PROCESSING PRODUCT: %s%n", i, productName);
                    System.out.println("Raw Groq response:\n" + aiResponse);

                    if (!aiResponse.trim().startsWith("{")) {
                        String newDescription="";
                        String newLine = String.join(",",
                                stockCode,
                                "\"" + productName.replace("\"", "\"\"") + "\"",
                                "\"" + aiResponse  + "\"",
                                "\"" + newDescription + "\""
                        );
                        outputLines.add(newLine);

                    }else{
                        JsonNode rootNode = mapper.readTree(aiResponse);

                        String newProductName = rootNode.path("productName").asText(productName);
                        JsonNode bulletsNode = rootNode.path("bullets");

                        StringBuilder bulletsBuilder = new StringBuilder();
                        if (bulletsNode.isArray()) {
                            for (JsonNode bullet : bulletsNode) {
                                if (bulletsBuilder.length() > 0) bulletsBuilder.append("; ");
                                bulletsBuilder.append(bullet.asText());
                            }
                        }

                        String newBullets = bulletsBuilder.toString();
                        String newDescription = rootNode.path("description").asText("Açıklama mevcut değil.");

                        String newLine = String.join(",",
                                stockCode,
                                "\"" + newProductName.replace("\"", "\"\"") + "\"",
                                "\"" + newBullets.replace("\"", "\"\"") + "\"",
                                "\"" + newDescription.replace("\"", "\"\"") + "\""
                        );
                        outputLines.add(newLine);
                        Thread.sleep(2100);
                    }

                } else {
                    outputLines.add(line); // keep original line if no processing
                }
            } catch (Exception ex) {
                System.err.printf("%d) ERROR: Failed to process '%s': %s%n", i, productName, ex.getMessage());

                String fallbackLine = String.join(",",
                        stockCode,
                        "\"" + productName.replace("\"", "\"\"") + "\"",
                        "\"error\"",
                        "\"error\""
                );
                outputLines.add(fallbackLine);
            }
        }

        Files.write(CSV_PATH, outputLines, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
        System.out.println("FINISHED");
    }

    private String buildPrompt(String productName) {
        return """
            You are a helpful assistant that ONLY outputs JSON.
            The following product name is a food or personal care product containing natural or organic ingredients. Create a simple and informative description in accordance with Turkish Food Codex and labeling regulations.

            Your task is to generate a JSON object containing:
            1.  "productName": The name of the product.
            2.  "bullets": An array of four strings answering the following, in order:
                a. What is the product?
                b. What is it traditionally used for?
                c. What are its general supportive effects on the body?
                d. How should it be used?
            3.  "description": A single string containing a detailed explanation of 100-150 words, combining the information from the bullet points into a coherent paragraph.

            Constraints for the content:
            -   Avoid sentences containing medical or definitive claims.
            -   Refrain from using bold expressions such as "100%% natural," "healing," or "miracle."
            -   All text MUST be in Turkish.

            Product Name: %s

            Respond ONLY with a valid JSON object structured exactly as shown below. Do NOT include any other text, explanations, or markdown before or after the JSON object.

            {
              "productName": "%s",
              "bullets": [
                "Turkish answer to point 2a",
                "Turkish answer to point 2b",
                "Turkish answer to point 2c",
                "Turkish answer to point 2d"
              ],
              "description": "Turkish detailed explanation of 200-250 words."
            }
            """.formatted(productName, productName);
    }

    public String getGroqResponse(String userMessage) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(groqConfig.getApiKey());
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", groqConfig.getModel());
        requestBody.put("messages", List.of(
                Map.of("role", "system", "content", "You are a helpful assistant that ONLY outputs JSON."),
                Map.of("role", "user", "content", userMessage)
        ));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(GROQ_API_URL, request, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                var choices = (List<Map<String, Object>>) response.getBody().get("choices");
                if (choices != null && !choices.isEmpty()) {
                    Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                    return (String) message.get("content");
                }
            }

        } catch (Exception e) {
            System.err.println("Groq API call failed: " + e.getMessage());
        }

        return "{\"productName\":\"\",\"bullets\":[],\"description\":\"\"}";
    }
}
