package com.yuce.chat.assistant.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class EmbeddingService {
    public List<Double> embed(String userPrompt) {
        return Collections.emptyList();
    }
}
