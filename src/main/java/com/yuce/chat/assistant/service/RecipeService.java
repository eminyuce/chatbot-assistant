package com.yuce.chat.assistant.service;

import com.yuce.chat.assistant.model.Event;
import com.yuce.chat.assistant.model.EventResponse;
import com.yuce.chat.assistant.model.IntentResult;
import com.yuce.chat.assistant.model.Parameters;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.yuce.chat.assistant.util.Constants.RECIPE;

@Service
@Slf4j
public class RecipeService {
    @Autowired
    private ChatModel chatModel;

    private String createRecipe(String foodName) {
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


    public Event createRecipe(IntentResult intent) {
        Parameters parameters = intent.getParameters();
        return new Event(RECIPE,
                new EventResponse(
                        this.createRecipe(parameters.getFoodName()
                        )));
    }
    public Event createRecipeStatic(IntentResult intent) {
        Parameters parameters = intent.getParameters();
        return new Event(RECIPE,
                new EventResponse("FOOD NAME:"+
                        parameters.getFoodName()));
    }
}