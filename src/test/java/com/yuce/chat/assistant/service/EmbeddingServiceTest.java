package com.yuce.chat.assistant.service;

import com.yuce.chat.assistant.service.impl.EmbeddingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class EmbeddingServiceTest {

    private EmbeddingService embeddingService;

    @BeforeEach
    void setUp() {
        embeddingService = new EmbeddingService();

        // Mock word embeddings
        Map<String, double[]> mockEmbeddings = new HashMap<>();
        mockEmbeddings.put("test", new double[100]);
        mockEmbeddings.put("hello", new double[100]);
        mockEmbeddings.put("weather", new double[100]);
        for (int i = 0; i < 100; i++) {
            mockEmbeddings.get("test")[i] = 1.0;
            mockEmbeddings.get("hello")[i] = 2.0;
            mockEmbeddings.get("weather")[i] = 2.0;
        }

        // Mock stopwords
        Set<String> mockStopWords = new HashSet<>();
        mockStopWords.add("the");
        mockStopWords.add("is");

        // Inject mocks into the service
        ReflectionTestUtils.setField(embeddingService, "wordEmbeddings", mockEmbeddings);
        ReflectionTestUtils.setField(embeddingService, "stopWords", mockStopWords);
    }

    @Test
    void testGenerateEmbedding_withValidWordsWeather() {
        List<Double> embedding = embeddingService.generateEmbedding("What is the weather in New York City?");

        assertNotNull(embedding);
        assertEquals(100, embedding.size());
        for (Double value : embedding) {
            assertEquals(2.0, value, 0.001); // Average of 1.0 and 2.0
        }
    }

    @Test
    void testGenerateEmbedding_withValidWords() {
        List<Double> embedding = embeddingService.generateEmbedding("hello test");

        assertNotNull(embedding);
        assertEquals(100, embedding.size());
        for (Double value : embedding) {
            assertEquals(1.5, value, 0.001); // Average of 1.0 and 2.0
        }
    }

    @Test
    void testGenerateEmbedding_withOnlyStopWords() {
        List<Double> embedding = embeddingService.generateEmbedding("the is");

        assertNotNull(embedding);
        assertEquals(100, embedding.size());
        assertTrue(embedding.stream().allMatch(val -> val == 0.0));
    }

    @Test
    void testGenerateEmbedding_withUnknownWords() {
        List<Double> embedding = embeddingService.generateEmbedding("foobar");

        assertNotNull(embedding);
        assertEquals(100, embedding.size());
        assertTrue(embedding.stream().allMatch(val -> val == 0.0));
    }

    @Test
    void testGenerateEmbedding_whenEmbeddingsNotLoaded() {
        ReflectionTestUtils.setField(embeddingService, "wordEmbeddings", new HashMap<>());

        List<Double> embedding = embeddingService.generateEmbedding("hello test");

        assertNotNull(embedding);
        assertEquals(100, embedding.size());
        assertTrue(embedding.stream().allMatch(val -> val == 0.0));
    }
}
