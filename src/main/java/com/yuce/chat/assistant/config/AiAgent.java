package com.yuce.chat.assistant.config;

import com.yuce.chat.assistant.model.GetNewsByPreferenceRequest;
import com.yuce.chat.assistant.model.GetNewsByPreferenceResponse;
import com.yuce.chat.assistant.model.UserPreferenceRequest;
import com.yuce.chat.assistant.model.UserPreferenceResponse;
import com.yuce.chat.assistant.persistence.repository.UserPreferencesRepository;
import com.yuce.chat.assistant.service.agent.GetNewsByUserPreferences;
import com.yuce.chat.assistant.service.agent.UserPreferencesService;
import com.yuce.chat.assistant.service.impl.NewsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

import java.util.function.Function;

@Configuration
public class AiAgent {


    public static final String GET_USER_PREFERENCES_FUNCTION_NAME = "getUserPreferences";
    public static final String GET_LATEST_NEWS_BY_TOPIC_FUNCTION_NAME = "getLatestNewsByTopic";

    @Bean(name = GET_USER_PREFERENCES_FUNCTION_NAME)
    @Description("Get topic by userId")
    public Function<UserPreferenceRequest, UserPreferenceResponse> getUserPreferencesInfo(
            UserPreferencesRepository userPreferencesRepository) {
        return new UserPreferencesService(userPreferencesRepository);
    }

    @Bean(name = GET_LATEST_NEWS_BY_TOPIC_FUNCTION_NAME)
    @Description("Get latest news from user topic")
    public Function<GetNewsByPreferenceRequest, GetNewsByPreferenceResponse> getNewsFromPreference(NewsService newsService) {
        return new GetNewsByUserPreferences(newsService);
    }

}
