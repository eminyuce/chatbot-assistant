package com.yuce.chat.assistant.persistence.repository;

import com.yuce.chat.assistant.persistence.entity.Drug;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DrugRepository extends JpaRepository<Drug, Long> {

    Drug findByName(String drugName);
}
