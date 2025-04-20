package com.yuce.chat.assistant.controller;


import com.yuce.chat.assistant.model.Event;
import com.yuce.chat.assistant.model.EventResponse;
import com.yuce.chat.assistant.model.IChatMessage;
import com.yuce.chat.assistant.service.ChatService;
import com.yuce.chat.assistant.service.RecipeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:4200", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.HEAD, RequestMethod.DELETE, RequestMethod.OPTIONS, RequestMethod.PATCH, RequestMethod.PUT, RequestMethod.TRACE})
@RestController
@RequestMapping("/gen-ai")
@Slf4j
public class GenAIController {

    @Autowired
    private ChatService chatService;
    @Autowired
    private RecipeService recipeService;

    @GetMapping(value = "ask-ai", produces = MediaType.APPLICATION_JSON_VALUE)
    public Event getResponse(@RequestParam String prompt) {
        return chatService.getResponseStream(IChatMessage.builder().prompt(prompt).build());
    }

  @PostMapping(value = "ask-ai-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public ResponseEntity<Flux<ServerSentEvent<EventResponse>>> getResponseStream(@RequestBody IChatMessage iChatMessage) {
        Event result = chatService.getResponseStream(iChatMessage);

        // Option 1: Send a single event with specific name and data
        var sseEvent = ServerSentEvent.<EventResponse>builder()
                .event(result.getType()) // Explicitly set the event name
                .data(result.getEventResponse()) // Set the data
                // .id(String.valueOf(System.currentTimeMillis())) // Optional: Event ID
                // .retry(Duration.ofSeconds(10)) // Optional: Client retry delay
                .build();

        var stream = Flux.just(sseEvent)
                .delayElements(Duration.ofMillis(50)); // Small delay helps ensure stream nature

        return ResponseEntity.ok(stream);
    }


    @PostMapping(value = "/ask-ai-tool", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<Flux<ServerSentEvent<EventResponse>>> askAgent(@RequestBody IChatMessage iChatMessage) {
        var result = chatService.callTools(iChatMessage);
        // Option 1: Send a single event with specific name and data
        var sseEvent = ServerSentEvent.<EventResponse>builder()
                .event(result.getType()) // Explicitly set the event name
                .data(result.getEventResponse()) // Set the data
                // .id(String.valueOf(System.currentTimeMillis())) // Optional: Event ID
                // .retry(Duration.ofSeconds(10)) // Optional: Client retry delay
                .build();

        var stream = Flux.just(sseEvent)
                .delayElements(Duration.ofMillis(50)); // Small delay helps ensure stream nature

        return ResponseEntity.ok(stream);
    }

    @GetMapping("recipe-creator")
    public String recipeCreator(@RequestParam String ingredients,
                                @RequestParam(defaultValue = "any") String cuisine,
                                @RequestParam(defaultValue = "") String dietaryRestriction) {
        return recipeService.createRecipe(ingredients, cuisine, dietaryRestriction);
    }
}