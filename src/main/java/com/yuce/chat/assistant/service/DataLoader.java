package com.yuce.chat.assistant.service;

import com.yuce.chat.assistant.persistence.entity.Intent;
import com.yuce.chat.assistant.persistence.repository.IntentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final IntentRepository intentRepository;
    private final IntentMatchingService intentMatchingService;

    private static final String WEATHER_INSTRUCTIONS = """
        You are an assistant that determines the intent of a user prompt and extracts relevant parameters for the 'weather' intent.
        Return a JSON response with the following structure:
        {
          "intent": "weather",
          "sub_intent": "no_sub_intent",
          "parameters": {
            "city": string | null,
            "symbol": null,
            "title": null,
            "author": null,
            "year": null,
            "food_name": null,
            "drug_name": null
          }
        }
        - Set "city" to the city name if provided, otherwise null.
        - All other parameters should be explicitly null.
        """;

    private static final String BOOK_INSTRUCTIONS = """
        You are an assistant that determines the intent of a user prompt and extracts relevant parameters for the 'book' intent.
        Return a JSON response with the following structure:
        {
          "intent": "book",
          "sub_intent": "add_book" | "update_book" | "delete_book" | "no_sub_intent",
          "parameters": {
            "city": null,
            "symbol": null,
            "title": string | null,
            "author": string | null,
            "year": number | null,
            "food_name": null,
            "drug_name": null
          }
        }
        - Determine the sub_intent from keywords like 'add', 'update', 'delete'.
        - Set "title", "author", and "year" as needed. All other parameters should be null.
        """;

    private static final String STOCK_INSTRUCTIONS = """
        You are an assistant that determines the intent of a user prompt and extracts relevant parameters for the 'stock_price' intent.
        Return a JSON response with the following structure:
        {
          "intent": "stock_price",
          "sub_intent": "no_sub_intent",
          "parameters": {
            "city": null,
            "symbol": string | null,
            "title": null,
            "author": null,
            "year": null,
            "food_name": null,
            "drug_name": null
          }
        }
        - Set "symbol" to the stock ticker symbol if provided.
        - All other parameters should be explicitly null.
        """;

    private static final String RECIPE_INSTRUCTIONS = """
        You are an assistant that determines the intent of a user prompt and extracts relevant parameters for the 'recipe' intent.
        Return a JSON response with the following structure:
        {
          "intent": "recipe",
          "sub_intent": "no_sub_intent",
          "parameters": {
            "city": null,
            "symbol": null,
            "title": null,
            "author": null,
            "year": null,
            "food_name": string | null,
            "drug_name": null
          }
        }
        - Set "food_name" to the name of the dish if provided, otherwise null.
        - All other parameters should be explicitly null.
        """;

    private static final String DRUG_INSTRUCTIONS = """
        You are an assistant that determines the intent of a user prompt and extracts relevant parameters for the 'drug' intent.
        Return a JSON response with the following structure:
        {
          "intent": "drug",
          "sub_intent": "no_sub_intent",
          "parameters": {
            "city": null,
            "symbol": null,
            "title": null,
            "author": null,
            "year": null,
            "food_name": null,
            "drug_name": string | null
          }
        }
        - Set "drug_name" to the drug name if provided.
        - All other parameters should be explicitly null.
        """;

    private static final String CHAT_BOT_USERS_INSTRUCTIONS = """
        You are an assistant that determines the intent of a user prompt related to 'chat_bot_users'.
        Return a JSON response with the following structure:
        {
          "intent": "chat_bot_users",
          "sub_intent": "no_sub_intent",
          "parameters": null
        }
        - No parameter extraction is required for this intent.
        """;

    private static final String GENERAL_INSTRUCTIONS = """
        You are an assistant that determines the intent of a user prompt. If the prompt does not match any of the specific intents (weather, book, stock_price, recipe, drug, chat_bot_users), classify it as 'general'.
        Return a JSON response with the following structure:
        {
          "intent": "general",
          "sub_intent": "no_sub_intent",
          "parameters": {
            "city": null,
            "symbol": null,
            "title": null,
            "author": null,
            "year": null,
            "food_name": null,
            "drug_name": null
          }
        }
        """;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Checking and loading initial intents...");

        createIntentIfNotFound("weather", "User is asking about the weather conditions.", WEATHER_INSTRUCTIONS);
        createIntentIfNotFound("book", "User wants to add, update, or delete book information.", BOOK_INSTRUCTIONS);
        createIntentIfNotFound("stock_price", "User is asking for the price of a stock.", STOCK_INSTRUCTIONS);
        createIntentIfNotFound("recipe", "User is asking for a recipe.", RECIPE_INSTRUCTIONS);
        createIntentIfNotFound("drug", "User is asking about a specific drug.", DRUG_INSTRUCTIONS);
        createIntentIfNotFound("chat_bot_users", "User is asking about chat bot users.", CHAT_BOT_USERS_INSTRUCTIONS);
        createIntentIfNotFound("general", "User has a general query not covered by other categories.", GENERAL_INSTRUCTIONS);

        log.info("Initial intent check complete. Triggering embedding generation for missing ones.");
        intentMatchingService.generateAndStoreEmbeddingsForMissing();
    }

    private void createIntentIfNotFound(String name, String description, String instructions) {
        if (intentRepository.findByIntentName(name).isEmpty()) {
            Intent intent = new Intent();
            intent.setIntentName(name);
            intent.setDescription(description);
            intent.setLlmInstructions(instructions);
            intentRepository.save(intent);
            log.info("Created initial intent: {}", name);
        }
    }
}
