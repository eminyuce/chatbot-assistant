package com.yuce.chat.assistant.service.agent;

import com.yuce.chat.assistant.model.UserPreferenceRequest;
import com.yuce.chat.assistant.model.UserPreferenceResponse;
import com.yuce.chat.assistant.persistence.repository.UserPreferencesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

public class UserPreferencesService implements Function<UserPreferenceRequest, UserPreferenceResponse> {

    private static final Logger logger = LoggerFactory.getLogger(UserPreferencesService.class);

    private final UserPreferencesRepository userPreferencesRepository;

    public UserPreferencesService(UserPreferencesRepository userPreferencesRepository) {
        this.userPreferencesRepository = userPreferencesRepository;
    }

    @Override
    public UserPreferenceResponse apply(UserPreferenceRequest userPreferenceRequest) {
        logger.info("UserPreference: {}", userPreferenceRequest);
        var preference = userPreferencesRepository.findById(userPreferenceRequest.userId())
                .orElseThrow(() -> new IllegalArgumentException("User preferences not found"));
        logger.info("User preferences: {}", preference);
        var topic = preference.getCategory();
        return new UserPreferenceResponse(topic);
    }
}
