package com.yuce.chat.assistant.persistence.repository;

import com.pgvector.PGvector;
import com.yuce.chat.assistant.persistence.entity.Intent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IntentRepository extends JpaRepository<Intent, Long> {

    // Native query for Cosine Distance similarity search using pgvector operator
    // Adjust '<=>' for other distance metrics if needed (e.g., '<#>' for inner product, '<->' for L2)
    // NOTE: Ensure the embedding dimension in the table matches the parameter vector dimension
    @Query(value = "SELECT * FROM intents ORDER BY embedding <=> CAST(:embedding AS vector) LIMIT 1", nativeQuery = true)
    Optional<Intent> findMostSimilarIntent(@Param("embedding") PGvector embedding);

    Optional<Intent> findByIntentName(String intentName);

    // Optional: Method to find intents without embeddings (for initialization)
    List<Intent> findByEmbeddingIsNull();
}