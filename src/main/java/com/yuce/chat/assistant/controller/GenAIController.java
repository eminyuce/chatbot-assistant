package com.yuce.chat.assistant.controller;


import com.yuce.chat.assistant.service.ChatService;
import com.yuce.chat.assistant.service.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.Map;

@RestController
@RequestMapping("/gen-ai")
public class GenAIController {

    @Autowired
    private ChatService chatService;
    @Autowired
    private RecipeService recipeService;

    @GetMapping("ask-ai")
    public String getResponse(@RequestParam String prompt) {
        return chatService.getResponse(prompt);
    }

    @GetMapping("ask-ai-options")
    public String getResponseOptions(@RequestParam String prompt) {
        return chatService.getResponseOptions(prompt);
    }

    @GetMapping("ask-ai-stream")
    public Flux<String> getResponseStream(@RequestParam String prompt) {
        return chatService.getResponseStream(prompt);
    }

    @PostMapping(value = "/ask", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> askAgent(@RequestBody Map<String, String> payload) {
        String question = payload.get("question");

        return chatService.getResponseStream(question);
    }

    @GetMapping("/chat-tool-function")
    public String callToolsMethods(@RequestParam(value = "query") String query) {
        return chatService.callTools(query);
    }

    @GetMapping("recipe-creator")
    public String recipeCreator(@RequestParam String ingredients,
                                @RequestParam(defaultValue = "any") String cuisine,
                                @RequestParam(defaultValue = "") String dietaryRestriction) {
        return recipeService.createRecipe(ingredients, cuisine, dietaryRestriction);
    }
}