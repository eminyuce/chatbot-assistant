package com.yuce.chat.assistant.controller;

import com.yuce.chat.assistant.model.IChatMessage;
import com.yuce.chat.assistant.model.IntentExtractionResult;
import com.yuce.chat.assistant.service.impl.IntentMatchingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/intent")
@RequiredArgsConstructor
public class IntentController {

    private final IntentMatchingService intentMatchingService;

    @PostMapping("/determine")
    public ResponseEntity<IntentExtractionResult> determineIntent(@RequestBody IChatMessage request) {
        if (request == null || request.getPrompt() == null || request.getPrompt().isBlank()) {
            return ResponseEntity.badRequest().build(); // Basic validation
        }
        IntentExtractionResult result = intentMatchingService.extractIntention(request);
        return ResponseEntity.ok(result);
    }

    // Optional: Endpoint to trigger embedding generation if needed
    @PostMapping("/admin/generate-embeddings")
    public ResponseEntity<String> generateEmbeddings() {
        intentMatchingService.generateAndStoreEmbeddingsForMissing();
        return ResponseEntity.ok("Embedding generation process initiated.");
    }
}