package com.yuce.chat.assistant.feign;


import com.yuce.chat.assistant.config.FeignClientConfig;
import com.yuce.chat.assistant.model.WeatherResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "weatherClient",
        url = "${feign.weather.client.url}",
        configuration = FeignClientConfig.class)
public interface WeatherClient {

    @CircuitBreaker(name = "weatherClient", fallbackMethod = "getWeatherFallback")
    @Retry(name = "weatherClient", fallbackMethod = "getWeatherFallback")
    @GetMapping("/weather")
    ResponseEntity<WeatherResponse> getWeather(
            @RequestParam("q") String city,
            @RequestParam("units") String units
    );

    default ResponseEntity<WeatherResponse> getWeatherFallback(String city, String units, Throwable t) {
        WeatherResponse fallbackResponse = new WeatherResponse();
        fallbackResponse.setName(city);
        WeatherResponse.Main main = new WeatherResponse.Main();
        main.setTemp(0.0);
        main.setHumidity(0);
        fallbackResponse.setMain(main);
        return ResponseEntity.ok(fallbackResponse);
    }
}