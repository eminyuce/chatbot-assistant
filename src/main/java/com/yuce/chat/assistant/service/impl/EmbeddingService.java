package com.yuce.chat.assistant.service.impl;


import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class EmbeddingService {

    private Map<String, double[]> wordEmbeddings;
    private Set<String> stopWords;
    private final EnglishAnalyzer englishAnalyzer = new EnglishAnalyzer();
    private static final int EMBEDDING_DIMENSION = 100; // Adjust based on your pre-trained model

    public EmbeddingService() {
        loadWordEmbeddings("C:\\Embedding\\glove.6B.100d.txt");
        loadStopWords("C:\\Embedding\\english.txt");
    }

    private void loadWordEmbeddings(String filePath) {
        wordEmbeddings = new HashMap<>();
        try {
            File file = ResourceUtils.getFile(filePath);
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split("\\s+");
                    if (parts.length > EMBEDDING_DIMENSION) {
                        String word = parts[0].toLowerCase().trim();
                        double[] vector = new double[EMBEDDING_DIMENSION];
                        for (int i = 0; i < EMBEDDING_DIMENSION; i++) {
                            vector[i] = Double.parseDouble(parts[i + 1]);
                        }
                        wordEmbeddings.put(word, vector);
                    }
                }
            }
            log.info("Loaded {} word embeddings.", wordEmbeddings.size());
        } catch (IOException e) {
            log.error("Error loading word embeddings from {}: {}", filePath, e.getMessage());
        }
    }

    private void loadStopWords(String filePath) {
        stopWords = new HashSet<>();
        try {
            File file = ResourceUtils.getFile(filePath);
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    stopWords.add(line.trim());
                }
            }
            log.info("Loaded {} stop words.", stopWords.size());
        } catch (IOException e) {
            log.error("Error loading stop words from {}: {}", filePath, e.getMessage());
        }
    }

    public List<Double> generateEmbedding(String userPrompt) {
        if (wordEmbeddings == null || wordEmbeddings.isEmpty()) {
            log.warn("Word embeddings not loaded. Returning zero vector.");
            return Collections.nCopies(EMBEDDING_DIMENSION, 0.0);
        }
        List<String> tokens = Arrays.stream(userPrompt.toLowerCase().replaceAll("[^a-zA-Z0-9\\s]", "").split("\\s+"))
                .map(this::stemWord)
                .filter(token -> !stopWords.contains(token) && wordEmbeddings.containsKey(token))
                .collect(Collectors.toList());

        if (tokens.isEmpty()) {
            log.warn("No valid words found for embedding: {}", userPrompt);
            return Collections.nCopies(EMBEDDING_DIMENSION, 0.0);
        }

        double[] finalEmbedding = new double[EMBEDDING_DIMENSION];
        for (String token : tokens) {
            double[] wordVector = wordEmbeddings.get(token);
            for (int i = 0; i < EMBEDDING_DIMENSION; i++) {
                finalEmbedding[i] += wordVector[i];
            }
        }

        for (int i = 0; i < EMBEDDING_DIMENSION; i++) {
            finalEmbedding[i] /= tokens.size();
        }

        List<Double> embeddingList = Arrays.stream(finalEmbedding)
                .boxed()
                .collect(Collectors.toList());

        log.info("Generated embedding (averaged word vectors) for: {}", userPrompt);
        return embeddingList;
    }

    private String stemWord(String word) {
        try (TokenStream tokenStream = englishAnalyzer.tokenStream("field", word)) {
            tokenStream.reset();
            if (tokenStream.incrementToken()) {
                return tokenStream.getAttribute(CharTermAttribute.class).toString();
            }
            tokenStream.end();
        } catch (IOException e) {
            log.error("Error during stemming word '{}': {}", word, e.getMessage());
        }
        return word;
    }

}