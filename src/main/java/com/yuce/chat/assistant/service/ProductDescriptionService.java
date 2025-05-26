package com.yuce.chat.assistant.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductDescriptionService {

    private static final Path CSV_PATH = Path.of("C:/Users/YUCE/Desktop/Education/urunler.csv");

    @Autowired
    private ChatModel chatModel;

    public void generateDescriptions() throws IOException, InterruptedException {
        List<String> lines = Files.readAllLines(CSV_PATH);
        if (lines.isEmpty()) return;

        List<String> outputLines = new ArrayList<>();
        outputLines.add(lines.get(0)); // header

        ObjectMapper mapper = new ObjectMapper();

        for (int i = 1; i < lines.size(); i++) {

                String line = lines.get(i);
                String[] cols = line.split(",", -1);

                String stockCode = cols.length > 0 ? cols[0].trim() : "";
                String productName = cols.length > 1 ? cols[1].trim() : "";
                String bullets = cols.length > 2 ? cols[2].trim() : "";
                String description = cols.length > 3 ? cols[3].trim() : "";

            try {
                if (!productName.isBlank() && (bullets.isBlank() || bullets.trim().equals("\"error\""))) {
                    productName=productName.replaceAll("\"", "");
                    String prompt = buildPrompt(productName);
                    String aiResponse = callOpenAI(prompt);
                    System.out.println(i+")PROCESSING PRODUCT NAME:" + productName);
                    // JSON parse et
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
                    String newDescription = rootNode.path("description").asText();

                    // CSV satırı oluştur
                    String newLine = String.join(",",
                            stockCode,
                            "\"" + newProductName.replace("\"", "\"\"") + "\"",
                            "\"" + newBullets.replace("\"", "\"\"") + "\"",
                            "\"" + newDescription.replace("\"", "\"\"") + "\""
                    );
                    outputLines.add(newLine);

                    Thread.sleep(100); // rate limit için bekleme
                } else {
                    outputLines.add(line);
                }

            } catch (Exception ex) {
                System.out.println(i+")ERROR PRODUCT NAME:" + ex);
                String newBullets="error";
                String newLine = String.join(",",
                        stockCode,
                        "\"" + productName.replace("\"", "\"\"") + "\"",
                        "\"" + newBullets.replace("\"", "\"\"") + "\"",
                        "\"" + newBullets.replace("\"", "\"\"") + "\""
                );
                outputLines.add(newLine);
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
            -   Refrain from using bold expressions such as "100% natural," "healing," or "miracle."
            -   All text MUST be in Turkish.

            Product Name: [PRODUCT_NAME]

            Respond ONLY with a valid JSON object structured exactly as shown below. Do NOT include any other text, explanations, or markdown before or after the JSON object.

            {
              "productName": "[PRODUCT_NAME_HERE]",
              "bullets": [
                "Turkish answer to point 2a",
                "Turkish answer to point 2b",
                "Turkish answer to point 2c",
                "Turkish answer to point 2d"
              ],
              "description": "Turkish detailed explanation of 100-150 words."
            }
            """.replace("[PRODUCT_NAME]", productName)
                .replace("[PRODUCT_NAME_HERE]", productName); // Also replace in the example for clarity
    }


    private String callOpenAI(String prompt) throws IOException, InterruptedException {
        String response = chatModel.call(prompt);
        return response;
    }
}
