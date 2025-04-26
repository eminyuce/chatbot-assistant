package com.yuce.chat.assistant.service.agent;


import com.yuce.chat.assistant.model.*;
import com.yuce.chat.assistant.service.impl.NewsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.Function;


public class GetNewsByUserPreferences implements Function<GetNewsByPreferenceRequest, GetNewsByPreferenceResponse> {

    private static final String CRYPTO = "crypto";
    private static final String STOCKS = "stocks";

    private static final Logger logger = LoggerFactory.getLogger(GetNewsByUserPreferences.class);
    private final NewsService newsService;

    public GetNewsByUserPreferences(NewsService newsService) {
        this.newsService = newsService;
    }

    @Override
    public GetNewsByPreferenceResponse apply(GetNewsByPreferenceRequest getNewsByPreferenceRequest) {

        String topic = getNewsByPreferenceRequest.topic();
        GetNewsRequest request = null;
        if (topic.equals(CRYPTO)) {
            request = new GetNewsRequest(CRYPTO);
        }else {
            request = new GetNewsRequest(STOCKS);

        }

        logger.info("Request: {}", request);
        List<NewsAndSentimentals> news = newsService.getNews(request); // API Request
        return newsService.extractNewsAndGiveOpinion(news);  // Second LLM call
    }
}
