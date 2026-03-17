package com.yuce.chat.assistant.feign;

import com.yuce.chat.assistant.config.FeignClientConfig;
import com.yuce.chat.assistant.model.StockResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "stockClient",
        url = "${feign.stock.client.url}",
        configuration = FeignClientConfig.class)
public interface StockClient {

    @CircuitBreaker(name = "stockClient", fallbackMethod = "getStockPriceFallback")
    @Retry(name = "stockClient", fallbackMethod = "getStockPriceFallback")
    @GetMapping("/stock")
    ResponseEntity<StockResponse> getStockPrice(
            @RequestParam("symbol") String symbol
    );

    default ResponseEntity<StockResponse> getStockPriceFallback(String symbol, Throwable t) {
        StockResponse fallbackResponse = new StockResponse();
        StockResponse.GlobalQuote quote = new StockResponse.GlobalQuote();
        quote.setSymbol(symbol);
        quote.setPrice("N/A");
        fallbackResponse.setGlobalQuote(quote);
        return ResponseEntity.ok(fallbackResponse);
    }
}