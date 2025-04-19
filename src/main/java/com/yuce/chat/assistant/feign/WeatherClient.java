package com.yuce.chat.assistant.feign;


import com.yuce.chat.assistant.config.FeignClientConfig;
import com.yuce.chat.assistant.model.WeatherResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "weatherClient",
        url = "${feign.weather.client.url}",
        configuration = FeignClientConfig.class)
public interface WeatherClient {

    @GetMapping("/weather")
    ResponseEntity<WeatherResponse> getWeather(
            @RequestParam("q") String city,
            @RequestParam("units") String units
    );
}