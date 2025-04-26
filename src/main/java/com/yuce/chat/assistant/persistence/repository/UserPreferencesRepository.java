package com.yuce.chat.assistant.persistence.repository;

import com.yuce.chat.assistant.persistence.entity.UserPreferences;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPreferencesRepository extends ListCrudRepository<UserPreferences, Integer> {
}

