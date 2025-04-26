package com.yuce.chat.assistant.model;

public record NewsAndSentimentals(
        String title,
        String url,
        String timePublished,
        String summary,
        double overallSentimentScore,
        String overallSentimentLabel) {

}
