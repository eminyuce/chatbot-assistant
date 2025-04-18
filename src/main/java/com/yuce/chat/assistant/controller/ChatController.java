package com.yuce.chat.assistant.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat")
public class ChatController {



    @PostMapping("/ask")
    public String ask(@RequestBody String userPrompt) {
        return userPrompt;
    }
}