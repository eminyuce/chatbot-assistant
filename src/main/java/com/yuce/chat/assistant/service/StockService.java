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

import static com.yuce.chat.assistant.util.Constants.*;

@Service
@Slf4j
public class StockService {

    @Autowired
    private StockClient stockClient;

    public Event getStockPrice(String symbol) {
        return this.getStockPrice(IntentResult.builder().parameters(Parameters.builder().symbol(symbol).build()).build());
    }

    public Event getStockPrice(IntentResult intent) {
        var eventResponse = EventResponse.builder().build();
        if (intent.getParameters().getSymbol() != null) {
            try {
                var stock = stockClient.getStockPrice(intent.getParameters().getSymbol());
                return new Event(STOCK, eventResponse.setContent(FormatTextUtil.getInstance().formatStockResponse(stock.getBody())));
            } catch (Exception e) {
                return new Event(ERROR, eventResponse.setContent("Sorry, I couldn't fetch the stock price for " + intent.getParameters().getSymbol() + ". Please try again."));
            }
        } else {
            return new Event(ERROR, eventResponse.setContent("Please specify a stock ticker symbol."));
        }
    }

}
