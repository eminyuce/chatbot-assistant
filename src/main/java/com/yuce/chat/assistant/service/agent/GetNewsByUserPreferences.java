package com.yuce.chat.assistant.service.agent;


import com.yuce.chat.assistant.model.GetNewsByPreferenceRequest;
import com.yuce.chat.assistant.model.GetNewsByPreferenceResponse;
import com.yuce.chat.assistant.model.GetNewsRequest;
import com.yuce.chat.assistant.model.NewsAndSentimentals;
import com.yuce.chat.assistant.service.impl.NewsService;
import com.yuce.chat.assistant.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.Function;


public class GetNewsByUserPreferences implements Function<GetNewsByPreferenceRequest, GetNewsByPreferenceResponse> {



    private static final Logger logger = LoggerFactory.getLogger(GetNewsByUserPreferences.class);
    private final NewsService newsService;

    public GetNewsByUserPreferences(NewsService newsService) {
        this.newsService = newsService;
    }

    @Override
    public GetNewsByPreferenceResponse apply(GetNewsByPreferenceRequest getNewsByPreferenceRequest) {

        String topic = getNewsByPreferenceRequest.topic();
        GetNewsRequest request = null;
        if (topic.equals(Constants.CRYPTO)) {
            request = new GetNewsRequest(Constants.CRYPTO);
        } else {
            request = new GetNewsRequest(Constants.STOCKS);

        }

        logger.info("Request: {}", request);
        List<NewsAndSentimentals> news = newsService.getNews(request); // API Request
        return newsService.extractNewsAndGiveOpinion(news);  // Second LLM call
    }
}
