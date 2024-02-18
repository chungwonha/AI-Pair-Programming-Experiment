package com.cool.app.newssentimentanalysis.repository;

import com.cool.app.newssentimentanalysis.entity.SentimentScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SentimentScoreRepository extends JpaRepository<SentimentScore, Long> {
    @Query("SELECT s FROM SentimentScore s WHERE s.uniqueIdentifier = :uniqueIdentifier")
    Optional<SentimentScore> findByUniqueIdentifier(String uniqueIdentifier);

}
