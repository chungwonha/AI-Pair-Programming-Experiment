package com.cool.app.newssentimentanalysis.service;

import com.cool.app.newssentimentanalysis.entity.SentimentScore;
import com.cool.app.newssentimentanalysis.repository.SentimentScoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SentimentScoreService {

    private final SentimentScoreRepository sentimentScoreRepository;

    @Autowired
    public SentimentScoreService(SentimentScoreRepository sentimentScoreRepository) {
        this.sentimentScoreRepository = sentimentScoreRepository;
    }

    public List<SentimentScore> findAll() {
        return sentimentScoreRepository.findAll();
    }

    public Optional<SentimentScore> findById(Long id) {
        return sentimentScoreRepository.findById(id);
    }

    public SentimentScore save(SentimentScore sentimentScore) {
        return sentimentScoreRepository.save(sentimentScore);
    }

    public SentimentScore update(SentimentScore sentimentScore) {
        if (sentimentScore.getId() == null || !sentimentScoreRepository.existsById(sentimentScore.getId())) {
            throw new IllegalArgumentException("SentimentScore must exist to be updated");
        }
        return sentimentScoreRepository.save(sentimentScore);
    }

    public void deleteById(Long id) {
        sentimentScoreRepository.deleteById(id);
    }

    public Optional<SentimentScore> findByUniqueIdentifier(String uniqueIdentifier) {
        // Custom query to find a SentimentScore by its unique identifier
        // Assuming you have defined such a query in your repository
        return sentimentScoreRepository.findByUniqueIdentifier(uniqueIdentifier);
    }

    public void saveAll(List<SentimentScore> sentimentScores){
//        sentimentScoreRepository.saveAll(sentimentScores);
        this.saveSentimentScoreListIfNotExists(sentimentScores);
    }

    public Optional<SentimentScore> saveSentimentScoreIfNotExists(SentimentScore newScore) {
        String uniqueIdentifier = newScore.getUniqueIdentifier();
        Optional<SentimentScore> existingScore = sentimentScoreRepository.findByUniqueIdentifier(uniqueIdentifier);

        if (!existingScore.isPresent()) {
            // No existing score, so save the new one
            SentimentScore savedScore = sentimentScoreRepository.save(newScore);
            return Optional.of(savedScore);
        }

        // Returning empty to indicate no new score was saved (already exists)
        return Optional.empty();
    }

    public List<SentimentScore> saveSentimentScoreListIfNotExists(List<SentimentScore> scores) {
        List<SentimentScore> savedScores = new ArrayList<>();

        for (SentimentScore score : scores) {
            String uniqueIdentifier = score.getUniqueIdentifier();
            Optional<SentimentScore> existingScore = sentimentScoreRepository.findByUniqueIdentifier(uniqueIdentifier);

            if (!existingScore.isPresent()) {
                // No existing score, so save the new one
                SentimentScore savedScore = sentimentScoreRepository.save(score);
                savedScores.add(savedScore);
            }
        }

        return savedScores;
    }
}
