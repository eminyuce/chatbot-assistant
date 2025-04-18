package com.yuce.chat.assistant.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat")
@Slf4j
public class ChatController {



    @PostMapping(value ="/ask", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String ask(@RequestBody String userPrompt) {
        return userPrompt;
    }
}