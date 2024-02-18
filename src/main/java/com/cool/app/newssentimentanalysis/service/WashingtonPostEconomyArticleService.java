package com.cool.app.newssentimentanalysis.service;

import com.cool.app.newssentimentanalysis.controller.Util;
import com.cool.app.newssentimentanalysis.entity.WashingtonPostEconomyArticleEntity;
import com.cool.app.newssentimentanalysis.entity.WsjJobArticleEntity;
import com.cool.app.newssentimentanalysis.repository.WashingtonPostEconomyArticleRepository;
import com.cool.app.newssentimentanalysis.sentiment.SentimentAnalyzer;
import com.cool.app.newssentimentanalysis.entity.SentimentScore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class WashingtonPostEconomyArticleService extends GenericArticleService<WashingtonPostEconomyArticleEntity, WashingtonPostEconomyArticleRepository> {

    @Autowired
    public WashingtonPostEconomyArticleService(WashingtonPostEconomyArticleRepository repository, SentimentAnalyzer sentimentAnalyzer) {
        super(repository, sentimentAnalyzer);
    }

//    public Optional<WashingtonPostEconomyArticleEntity> getArticleByIdWithSentimentScore(int id) {
//        Function<WashingtonPostEconomyArticleEntity, String> getTextFunction = WashingtonPostEconomyArticleEntity::getArticleText;
//        return getArticleByIdWithSentimentScore(id, getTextFunction);
//    }

    @Override
    public Optional<SentimentScore> getSentimentScore(int id) {

        Optional<SentimentScore> sentimentScore = super.getSentimentScore(
                id,
                WashingtonPostEconomyArticleEntity::getArticleText,     // Method reference for getting text
                WashingtonPostEconomyArticleEntity::getArticleLinksHref, // Method reference for getting the unique identifier
                WashingtonPostEconomyArticleEntity::getPublishedDate,
                WashingtonPostEconomyArticleEntity::getArticleLinks,
                Util::convertDate1,
                WashingtonPostEconomyArticleEntity::getArticleType,
                WashingtonPostEconomyArticleEntity::getArticleSource
        );
        return sentimentScore;
    }

    @Override
    public List<SentimentScore> getSentimentScoreForAll() {
        return super.getSentimentScoreForAll( WashingtonPostEconomyArticleEntity::getArticleText,     // Method reference for getting text
                                                WashingtonPostEconomyArticleEntity::getArticleLinksHref,
                                                WashingtonPostEconomyArticleEntity::getPublishedDate,
                                                WashingtonPostEconomyArticleEntity::getArticleLinks,
                                                Util::convertDate1,
                                                WashingtonPostEconomyArticleEntity::getArticleType,
                                                WashingtonPostEconomyArticleEntity::getArticleSource);
    }

    //    public Optional<Double> getSentimentScoreById(int id) {
//        Function<WashingtonPostEconomyArticleEntity, String> getTextFunction = WashingtonPostEconomyArticleEntity::getArticleText;
//        return getSentimentScoreById(id, getTextFunction);
//    }
    public boolean checkArticleExists(String articleLinksHref) {
        return articleRepository.existsByArticleLinksHref(articleLinksHref);
    }

    public void cleansing() {
        this.updateDates();
    }

    public void updateDates() {
        articleRepository.updatePublishedDateFormats();

    }
}
