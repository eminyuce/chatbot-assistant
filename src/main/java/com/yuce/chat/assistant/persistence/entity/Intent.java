package com.yuce.chat.assistant.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "intents")
@Getter
@Setter
public class Intent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "intent_name", nullable = false, unique = true)
    private String intentName;

    @Column(nullable = false)
    private String description; // Text used for embedding

    @Column(name = "embedding") // Map to vector type
    private float[] embedding;

    @Column(name = "llm_instructions", nullable = false, columnDefinition = "TEXT")
    private String llmInstructions; // Detailed instructions for this intent
}