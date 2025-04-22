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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;

@RestController
@RequestMapping("/gen-ai")
@Slf4j
public class GenAIController {

    @Autowired
    private ChatService chatService;

    @PostMapping(value = "ask-ai", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Event> getResponse(@RequestBody IChatMessage iChatMessage) {
        Event event = chatService.getResponseStream(iChatMessage);
        return ResponseEntity.ok(event);
    }


    @PreAuthorize("hasRole('ANGULAR')")
    @PostMapping(value = "ask-ai-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<Flux<ServerSentEvent<EventResponse>>> getResponseStream(@RequestBody IChatMessage iChatMessage) {

        Event event = chatService.getResponseStream(iChatMessage);

        Flux<ServerSentEvent<EventResponse>> stream = Flux.just(event)
                .map(result -> ServerSentEvent.<EventResponse>builder()
                        .event(result.type())
                        .data(result.eventResponse())
                        .build())
                .onErrorResume(throwable -> Flux.just(
                        ServerSentEvent.<EventResponse>builder()
                                .event("error")
                                .data(new EventResponse("Error: " + throwable.getMessage()))
                                .build()))
                .delayElements(Duration.ofMillis(1));

        return ResponseEntity.ok(stream);
    }

    @PreAuthorize("hasRole('ANGULAR')")
    @PostMapping(value = "ask-ai-tool", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<Flux<ServerSentEvent<EventResponse>>> askAgent(@RequestBody IChatMessage iChatMessage) {
        var event = chatService.callTools(iChatMessage);
        // Option 1: Send a single event with specific name and data
        Flux<ServerSentEvent<EventResponse>> stream = Flux.just(event)
                .map(result -> ServerSentEvent.<EventResponse>builder()
                        .event(result.type())
                        .data(result.eventResponse())
                        .build())
                .onErrorResume(throwable -> Flux.just(
                        ServerSentEvent.<EventResponse>builder()
                                .event("error")
                                .data(new EventResponse("Error: " + throwable.getMessage()))
                                .build()))
                .delayElements(Duration.ofMillis(1));// Small delay helps ensure stream nature

        return ResponseEntity.ok(stream);
    }
}