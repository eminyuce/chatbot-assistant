package com.yuce.chat.assistant.service.impl;

import com.yuce.chat.assistant.model.Event;
import com.yuce.chat.assistant.model.EventResponse;
import com.yuce.chat.assistant.model.IntentExtractionResult;
import com.yuce.chat.assistant.model.Parameters;
import com.yuce.chat.assistant.service.IntentService;
import com.yuce.chat.assistant.service.RecipeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.yuce.chat.assistant.util.Constants.RECIPE;

@Service("recipe-service")
@Slf4j
public class RecipeServiceImpl implements RecipeService, IntentService {
    @Autowired
    private ChatModel chatModel;

    @Override
    public String createRecipe(String foodName) {
        String template = """
                I want to get a recipe for: %s.
                How do I cook it?
                What are the dietary restrictions?
                What are the ingredients? What should I buy from the grocery store?
                What is the best location to eat it if I want to go out in NYC?
                """;

        String formattedTemplate = String.format(template, foodName);
        Prompt intentPrompt = new Prompt(List.of(
                new UserMessage(formattedTemplate)
        ));
        String result = chatModel.call(intentPrompt).getResult().getOutput().getText();
        return result;
    }


    @Override
    public Event createRecipe(IntentExtractionResult intent) {
        Parameters parameters = intent.getParameters();
        return new Event(RECIPE,
                new EventResponse(
                        this.createRecipe(parameters.getFoodName()
                        )));
    }

    @Override
    public Event createRecipeStatic(IntentExtractionResult intent) {
        Parameters parameters = intent.getParameters();
        return new Event(RECIPE,
                new EventResponse("FOOD NAME:" +
                        parameters.getFoodName()));
    }

    @Override
    public Event run(IntentExtractionResult intent) {
        return this.createRecipe(intent);
    }
}