package com.yuce.chat.assistant.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yuce.chat.assistant.model.IntentExtractionResult;
import com.yuce.chat.assistant.model.Parameters;
import com.yuce.chat.assistant.persistence.entity.Intent;
import com.yuce.chat.assistant.persistence.repository.IntentRepository;
import com.yuce.chat.assistant.util.BeanOutputParser;
import com.yuce.chat.assistant.util.JsonExtractor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static org.springframework.ai.vectorstore.SimpleVectorStore.EmbeddingMath.cosineSimilarity;

@Service
@Slf4j
public class IntentMatchingService {

    @Autowired
    private IntentRepository intentRepository;
    @Autowired
    private EmbeddingService embeddingService;
    @Autowired
    private ChatClient chatClient; // Use Builder for potential customization

    @Autowired
    private ObjectMapper objectMapper;

    @Transactional(readOnly = true) // Read-only transaction for searching
    public IntentExtractionResult determineIntentAndExtract(String userPrompt) {
        // 1. Calculate embedding for the user prompt
        List<Double> userEmbeddingList = embeddingService.generateEmbedding(userPrompt);
        //PGvector userEmbedding = new PGvector(convertToFloatArray(userEmbeddingList));

        float[] inputVector = getFloats(userEmbeddingList);

        // 2. Find the most similar intent description in the database
        // Optional<Intent> mostSimilarIntentOpt = intentRepository.findMostSimilarIntent(userEmbedding);
        Optional<Intent> mostSimilarIntentOpt = intentRepository.findAll().stream()
                .max(Comparator.comparingDouble(i -> cosineSimilarity(i.getEmbedding(), inputVector)));

        if (mostSimilarIntentOpt.isEmpty()) {
            log.warn("No similar intent found for prompt: '{}'. Defaulting to general.", userPrompt);
            // Handle case where no intents are in DB or search fails - maybe default to "general" instructions
            return getDefaultGeneralIntentExtractionResult();
        }

        Intent matchedIntent = mostSimilarIntentOpt.get();
        log.info("Matched intent '{}' for prompt: '{}'", matchedIntent.getIntentName(), userPrompt);

        // 3. Get the specific LLM instructions for the matched intent
        String specificInstructions = matchedIntent.getLlmInstructions();

        // 4. Construct the prompt for the LLM using the matched instructions
        String llmPrompt = specificInstructions + "\n\nUser Prompt: \"" + userPrompt + "\"";

        // 5. Call the LLM with the specific instructions and user prompt
        // 5. Call the LLM with the specific instructions and user prompt
        // Use BeanOutputParser for reliable JSON parsing
        var outputParser = new BeanOutputParser<>(IntentExtractionResult.class, objectMapper);
        String formatInstructions = outputParser.getFormat(); // Get format instructions for the LLM

        Prompt prompt = new Prompt(llmPrompt + "\n\n" + formatInstructions);

        try {
            log.debug("Sending prompt to LLM for intent extraction:\n{}", llmPrompt);
            ChatResponse response = chatClient.prompt(prompt).call().chatResponse();

            // Parse the LLM's JSON response

            // Parse the LLM's JSON response
            String rawJsonResponse = response.getResult().getOutput().getText();
            String jsonResponse = JsonExtractor.extractJson(rawJsonResponse);
            IntentExtractionResult result = outputParser.parse(jsonResponse);
            log.debug("LLM extraction result: {}", result);
            return result;

        } catch (Exception e) {
            log.error("Error calling LLM or parsing response for prompt '{}': {}", userPrompt, e.getMessage(), e);
            // Fallback or error handling
            return getDefaultGeneralIntentExtractionResult("Error during LLM processing.");
        }
    }

    private static float[] getFloats(List<Double> userEmbeddingList) {
        Float[] data = userEmbeddingList.stream().map(x -> x.floatValue()).toArray(Float[]::new);
        float[] inputVector = new float[userEmbeddingList.size()];
        for (int x = 0; x < data.length; x++) {
            inputVector[x] = data[x].floatValue();
        }
        return inputVector;
    }

    // Helper to convert List<Double> to float[] for PGvector
    private float[] convertToFloatArray(List<Double> list) {
        if (list == null) {
            return null;
        }
        float[] array = new float[list.size()];
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i).floatValue();
        }
        return array;
    }

    private IntentExtractionResult getDefaultGeneralIntentExtractionResult(String errorDetails) {
        log.warn("Defaulting to general intent. Details: {}", errorDetails);
        return IntentExtractionResult.builder().intent("general").subIntent("no_sub_intent").parameters(new Parameters(null, null, null, null, null, null, null)).build();
    }

    private IntentExtractionResult getDefaultGeneralIntentExtractionResult() {
        return getDefaultGeneralIntentExtractionResult("No specific reason provided.");
    }

    // --- Optional: Initialization Logic ---
    @Transactional // Writable transaction for saving
    public void generateAndStoreEmbeddingsForMissing() {
        List<Intent> intentsWithoutEmbeddings = intentRepository.findByEmbeddingIsNull();
        if (!intentsWithoutEmbeddings.isEmpty()) {
            log.info("Found {} intents without embeddings. Generating...", intentsWithoutEmbeddings.size());
            for (Intent intent : intentsWithoutEmbeddings) {
                try {

                    List<Double> embeddingList = embeddingService.generateEmbedding(intent.getDescription()); // Embed the description
                    intent.setEmbedding(getFloats(embeddingList));

                    intentRepository.save(intent);
                    log.info("Generated and saved embedding for intent: {}", intent.getIntentName());
                } catch (Exception e) {
                    log.error("Failed to generate embedding for intent '{}': {}", intent.getIntentName(), e.getMessage(), e);
                    // Decide how to handle failures - skip, retry later?
                }
            }
            log.info("Finished generating missing embeddings.");
        } else {
            log.info("All intents seem to have embeddings already.");
        }
    }
}
