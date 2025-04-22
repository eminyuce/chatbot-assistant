package com.yuce.chat.assistant.service;

import com.yuce.chat.assistant.model.Event;
import com.yuce.chat.assistant.model.EventResponse;
import com.yuce.chat.assistant.model.IntentResult;
import com.yuce.chat.assistant.model.Parameters;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

import static com.yuce.chat.assistant.util.Constants.RECIPE;

@Service
@Slf4j
public class RecipeService {
    private final ChatModel chatModel;

    public RecipeService(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    private String createRecipe(String ingredients,
                                String cuisine,
                                String dietaryRestrictions) {
        var template = """
                I want to create a recipe using the following ingredients: {ingredients}.
                The cuisine type I prefer is {cuisine}.
                Please consider the following dietary restrictions: {dietaryRestrictions}.
                               
                Can you provide me with a detailed and easy-to-follow recipe that includes the following:
                               
                Recipe Title – something creative that reflects the cuisine or the main ingredient.
                               
                List of Ingredients – include precise measurements (metric or imperial) and any optional ingredients for flavor enhancements.
                               
                Cooking Instructions – step-by-step directions that cover preparation, cooking times, temperatures (if applicable), and plating suggestions.
                               
                Estimated Cooking Time – total time needed from start to finish.
                               
                Servings – how many people the recipe is for.
                               
                Tips or Substitutions – optional section with helpful tips, alternatives for unavailable ingredients, or ways to adjust the recipe to suit different tastes or diets.
                               
                Make sure the recipe aligns with the specified cuisine and dietary restrictions while creatively using the listed ingredients.
                                
                """;

        PromptTemplate promptTemplate = new PromptTemplate(template);
        Map<String, Object> params = Map.of(
                "ingredients", ingredients,
                "cuisine", cuisine,
                "dietaryRestrictions", dietaryRestrictions
        );

        Prompt prompt = promptTemplate.create(params);
        return chatModel.call(prompt).getResult().getOutput().getText();
    }

    public Event createRecipe(IntentResult intent) {
        Parameters parameters = intent.getParameters();
        return new Event(RECIPE,
                new EventResponse(
                        this.createRecipe(parameters.getIngredients(),
                                parameters.getCuisine(),
                                parameters.getDietaryRestrictions()
                        )));
    }
}