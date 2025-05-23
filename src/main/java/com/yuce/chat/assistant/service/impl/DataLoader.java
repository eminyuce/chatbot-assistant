package com.yuce.chat.assistant.service.impl;

import com.yuce.chat.assistant.persistence.entity.Book;
import com.yuce.chat.assistant.persistence.entity.Drug;
import com.yuce.chat.assistant.persistence.entity.Intent;
import com.yuce.chat.assistant.persistence.entity.UserPreferences;
import com.yuce.chat.assistant.persistence.repository.DrugRepository;
import com.yuce.chat.assistant.persistence.repository.IntentRepository;
import com.yuce.chat.assistant.persistence.repository.UserPreferencesRepository;
import com.yuce.chat.assistant.service.BookService;
import com.yuce.chat.assistant.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;

@Component
@Slf4j
public class DataLoader implements CommandLineRunner {

    @Autowired
    private IntentRepository intentRepository;

    @Autowired
    private DrugRepository drugRepository;
    @Autowired
    private UserPreferencesRepository userPreferencesRepository;
    @Autowired
    private IntentMatchingService intentMatchingService;
    @Autowired
    private BookService bookService;
    private static final String WEATHER_INSTRUCTIONS = """
            You are an assistant that determines the intent of a user prompt and extracts relevant parameters for the 'weather' intent.
            Return a  **valid JSON response** with the following structure:
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
            - Do **not** include any additional text, markdown, or code block formatting. Only return the **JSON structure** as described above.
                    
            """;

    private static final String BOOK_INSTRUCTIONS = """
            You are an assistant that determines the intent of a user prompt and extracts relevant parameters for the 'book' intent.
            User can add, remove, delete book and find book by its title, author or year
            Return a  **valid JSON response** with the following structure:
            {
              "intent": "book",
              "sub_intent": "add_book" | "update_book" | "delete_book" | "find_book" | "no_sub_intent",
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
            - Do **not** include any additional text, markdown, or code block formatting. Only return the **JSON structure** as described above.
                    
            """;

    private static final String STOCK_INSTRUCTIONS = """
            You are an assistant that determines the intent of a user prompt and extracts relevant parameters for the 'stock_price' intent.
            Return a  **valid JSON response** with the following structure:
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
            - Do **not** include any additional text, markdown, or code block formatting. Only return the **JSON structure** as described above.
                    
            """;

    private static final String RECIPE_INSTRUCTIONS = """
            You are an assistant that determines the intent of a user prompt and extracts relevant parameters for the 'recipe' intent.
            Return a **valid JSON response** with the following structure:
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
            - Do **not** include any additional text, markdown, or code block formatting. Only return the **JSON structure** as described above.
                    
            """;

    private static final String DRUG_INSTRUCTIONS = """
            You are an assistant that determines the intent of a user prompt and extracts relevant parameters for the 'drug' intent.
                       
            Return a **valid JSON response** with the following structure:
                       
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
                       
            - Set the value of "drug_name" to the drug name if provided in the prompt, otherwise, set it to `null`.
            - Explicitly set all other parameters to `null`.
            - Do **not** include any additional text, markdown, or code block formatting. Only return the **JSON structure** as described above.
            """;

    private static final String CHAT_BOT_USERS_INSTRUCTIONS = """
            You are an assistant that determines the intent of a user prompt related to 'chat_bot_users'.
            Return a  **valid JSON response** with the following structure:
            {
              "intent": "chat_bot_users",
              "sub_intent": "no_sub_intent",
              "parameters": null
            }
            - No parameter extraction is required for this intent.
            - Do **not** include any additional text, markdown, or code block formatting. Only return the **JSON structure** as described above.
                    
            """;

    private static final String GENERAL_INSTRUCTIONS = """
            You are an assistant that determines the intent of a user prompt. If the prompt does not match any of the specific intents (weather, book, stock_price, recipe, drug, chat_bot_users), classify it as 'general'.
            Return a **valid JSON response** with the following structure:
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
                        
            - Do **not** include any additional text, markdown, or code block formatting. Only return the **JSON structure** as described above.
                    
            """;

    @Override
    @Transactional
    public void run(String... args) {
        initIntents();
        initBooks();
        initUserPref();
        initDrugs();
    }

    @Transactional
    private void initDrugs() {
        // Save multiple drugs into the database with the current date as the expiration date
        drugRepository.save(new Drug("Aspirin", "Pain reliever and anti-inflammatory", Date.valueOf(LocalDate.now())));
        drugRepository.save(new Drug("Ibuprofen", "Nonsteroidal anti-inflammatory drug", Date.valueOf(LocalDate.now())));
        drugRepository.save(new Drug("Paracetamol", "Pain reliever and fever reducer", Date.valueOf(LocalDate.now())));
        drugRepository.save(new Drug("Amoxicillin", "Antibiotic for bacterial infections", Date.valueOf(LocalDate.now())));
        drugRepository.save(new Drug("Cetirizine", "Antihistamine for allergy relief", Date.valueOf(LocalDate.now())));
        drugRepository.save(new Drug("Metformin", "Oral medication for type 2 diabetes", Date.valueOf(LocalDate.now())));
        drugRepository.save(new Drug("Simvastatin", "Cholesterol-lowering medication", Date.valueOf(LocalDate.now())));
        drugRepository.save(new Drug("Loratadine", "Antihistamine for allergy symptoms", Date.valueOf(LocalDate.now())));
        drugRepository.save(new Drug("Losartan", "Medications for hypertension", Date.valueOf(LocalDate.now())));
        drugRepository.save(new Drug("Omeprazole", "Proton pump inhibitor for acid reflux", Date.valueOf(LocalDate.now())));
    }


    private void initUserPref() {
        if (userPreferencesRepository.findById(1).isEmpty())
            userPreferencesRepository.save(new UserPreferences(Constants.CRYPTO));
        if (userPreferencesRepository.findById(2).isEmpty())
            userPreferencesRepository.save(new UserPreferences(Constants.STOCKS));
    }

    private void initBooks() {
        if (bookService.findBookByTitle("Learn Java Programming") == null) {
            var book = new Book();
            bookService.addBook(book.setAuthor("Emin YUCE").setYear(1998).setTitle("Learn Java Programming"));
        }
        if (bookService.findBookByTitle("Learn C Programming") == null) {
            var book = new Book();
            bookService.addBook(book.setAuthor("Emin YUCE").setYear(1998).setTitle("Learn C Programming"));
        }
        if (bookService.findBookByTitle("Learn C# Programming") == null) {
            var book = new Book();
            bookService.addBook(book.setAuthor("Emin YUCE").setYear(1998).setTitle("Learn C# Programming"));
        }
    }

    private void initIntents() {
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