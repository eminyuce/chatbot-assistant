package com.yuce.chat.assistant.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yuce.chat.assistant.feign.StockClient;
import com.yuce.chat.assistant.feign.WeatherClient;
import com.yuce.chat.assistant.model.Event;
import com.yuce.chat.assistant.model.StockResponse;
import com.yuce.chat.assistant.model.WeatherResponse;
import com.yuce.chat.assistant.tool.AiToolService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.yuce.chat.assistant.util.Constants.*;

@Service
public class ChatService {


    @Autowired
    private ChatModel chatModel;

    @Autowired
    private ChatClient chatClient;

    @Autowired
    private WeatherClient weatherClient;

    @Autowired
    private StockClient stockClient;

    @Value("classpath:/prompts/intent-message.st")
    private Resource intentMessageResource;

    @Autowired
    private AiToolService aiToolService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String getResponse(String prompt) {
        IntentResult intent = detectIntent(prompt);
        switch (intent.intent) {
            case WEATHER:
                if (intent.parameters.city != null) {
                    try {
                        var weather = weatherClient.getWeather(intent.parameters.city, "metric");
                        return formatWeatherResponse(weather.getBody());
                    } catch (Exception e) {
                        return "Sorry, I couldn't fetch the weather for " + intent.parameters.city + ". Please try again.";
                    }
                } else {
                    return "Please specify a city for the weather query.";
                }
            case STOCK_PRICE:
                if (intent.parameters.symbol != null) {
                    try {
                        var stock = stockClient.getStockPrice(intent.parameters.symbol);
                        return formatStockResponse(stock.getBody());
                    } catch (Exception e) {
                        return "Sorry, I couldn't fetch the stock price for " + intent.parameters.symbol + ". Please try again.";
                    }
                } else {
                    return "Please specify a stock ticker symbol.";
                }
            default:
                return chatModel.call(prompt);
        }
    }

    public Event getResponseStream(String prompt) {
        IntentResult intent = detectIntent(prompt);

        switch (intent.intent) {
            case WEATHER:
                if (intent.parameters.city != null) {
                    try {
                        var weather = weatherClient.getWeather(intent.parameters.city, "metric");
                        return new Event("weather", formatWeatherResponse(weather.getBody()));
                    } catch (Exception e) {
                        return new Event("error", "Sorry, I couldn't fetch the weather for " + intent.parameters.city + ". Please try again.");
                    }
                } else {
                    return new Event("error", "Please specify a city for the weather query.");
                }

            case STOCK_PRICE:
                if (intent.parameters.symbol != null) {
                    try {
                        var stock = stockClient.getStockPrice(intent.parameters.symbol);
                        return new Event("stock", formatStockResponse(stock.getBody()));
                    } catch (Exception e) {
                        return new Event("error", "Sorry, I couldn't fetch the stock price for " + intent.parameters.symbol + ". Please try again.");
                    }
                } else {
                    return new Event("error", "Please specify a stock ticker symbol.");
                }

            default:
                String response = chatModel.call(prompt);
                return new Event("chat", response);
        }
    }



    private IntentResult detectIntent(String prompt) {
        try {
            Prompt intentPrompt = new Prompt(List.of(
                    new SystemMessage(intentMessageResource),
                    new UserMessage(prompt)
            ));
            String jsonResponse = chatModel.call(intentPrompt).getResult().getOutput().getText();
            Map<String, Object> result = objectMapper.readValue(jsonResponse, Map.class);
            String intent = (String) result.getOrDefault("intent", "general");
            Map<String, Object> parameters = (Map<String, Object>) result.getOrDefault("parameters", Map.of());
            String city = parameters.get("city") != null ? parameters.get("city").toString() : null;
            String symbol = parameters.get("symbol") != null ? parameters.get("symbol").toString() : null;
            return new IntentResult(intent, new Parameters(city, symbol));
        } catch (Exception e) {
            // Fallback to general query if intent detection fails
            return new IntentResult("general", new Parameters(null, null));
        }
    }


    public String callTools(String message) {
        UserMessage userMessage = new UserMessage(message);
        final Prompt prompt = new Prompt(List.of(new SystemMessage(intentMessageResource), userMessage));
        var result = chatClient
                .prompt(prompt)
                .tools(aiToolService) // Auto-detects @Tool-annotated methods
                .call()
                .content();
        return result;
    }

    private String formatWeatherResponse(WeatherResponse weather) {
        return String.format(
                "The weather in %s is %.1f°C with %d%% humidity.",
                weather.getName(),
                weather.getMain().getTemp(),
                weather.getMain().getHumidity()
        );
    }

    private String formatStockResponse(StockResponse stock) {
        return String.format(
                "The stock price of %s is $%s.",
                stock.getGlobalQuote().getSymbol(),
                stock.getGlobalQuote().getPrice()
        );
    }



    private static class IntentResult {
        String intent;
        Parameters parameters;

        IntentResult(String intent, Parameters parameters) {
            this.intent = intent;
            this.parameters = parameters;
        }
    }

    private static class Parameters {
        String city;
        String symbol;

        Parameters(String city, String symbol) {
            this.city = city;
            this.symbol = symbol;
        }
    }
}