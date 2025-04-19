package com.yuce.chat.assistant.feign;

import com.yuce.chat.assistant.config.FeignClientConfig;
import com.yuce.chat.assistant.model.StockResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "stockClient",
        url = "${feign.stock.client.url}",
        configuration = FeignClientConfig.class)
public interface StockClient {

    @GetMapping("/stock")
    ResponseEntity<StockResponse> getStockPrice(
            @RequestParam("symbol") String symbol
    );
}