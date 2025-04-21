package com.yuce.chat.assistant.service;

import com.yuce.chat.assistant.feign.StockClient;
import com.yuce.chat.assistant.model.Event;
import com.yuce.chat.assistant.model.EventResponse;
import com.yuce.chat.assistant.model.IntentResult;
import com.yuce.chat.assistant.model.Parameters;
import com.yuce.chat.assistant.util.FormatTextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.yuce.chat.assistant.util.Constants.ERROR;
import static com.yuce.chat.assistant.util.Constants.STOCK;

@Service
@Slf4j
public class StockService {

    @Autowired
    private StockClient stockClient;

    public Event getStockPrice(String symbol) {
        return this.getStockPrice(IntentResult.builder().parameters(Parameters.builder().symbol(symbol).build()).build());
    }

    public Event getStockPrice(IntentResult intent) {
        EventResponse eventResponse = new EventResponse(null); // Temporary placeholder

        // Check if intent or parameters are null
        if (intent == null || intent.getParameters() == null || intent.getParameters().getSymbol() == null) {
            return new Event(ERROR, new EventResponse("Please specify a stock ticker symbol."));
        }

        try {
            var stockResponse = stockClient.getStockPrice(intent.getParameters().getSymbol());
            if (stockResponse == null || stockResponse.getBody() == null) {
                return new Event(ERROR, new EventResponse("No stock data found for symbol: " + intent.getParameters().getSymbol()));
            }
            return new Event(STOCK, new EventResponse(FormatTextUtil.getInstance().formatStockResponse(stockResponse.getBody())));
        } catch (RuntimeException e) {
            return new Event(ERROR, new EventResponse("Failed to fetch stock price for " + intent.getParameters().getSymbol() + ": " + e.getMessage()));
        }
    }

}
