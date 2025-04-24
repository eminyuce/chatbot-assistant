package com.yuce.chat.assistant.service;

import com.yuce.chat.assistant.model.Event;
import com.yuce.chat.assistant.model.IntentExtractionResult;
import org.springframework.stereotype.Service;

@Service
public interface RecipeService {

    String createRecipe(String foodName);

    Event createRecipe(IntentExtractionResult intent);

    Event createRecipeStatic(IntentExtractionResult intent);
}
