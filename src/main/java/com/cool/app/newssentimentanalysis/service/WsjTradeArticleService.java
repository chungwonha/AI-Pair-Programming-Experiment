package com.cool.app.newssentimentanalysis.service;

import com.cool.app.newssentimentanalysis.controller.Util;
import com.cool.app.newssentimentanalysis.entity.SentimentScore;
import com.cool.app.newssentimentanalysis.entity.WsjTradeArticleEntity;
import com.cool.app.newssentimentanalysis.repository.WsjTradeArticleRepository;
import com.cool.app.newssentimentanalysis.sentiment.SentimentAnalyzer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WsjTradeArticleService extends GenericArticleService<WsjTradeArticleEntity, WsjTradeArticleRepository> {
    @Autowired
    public WsjTradeArticleService(WsjTradeArticleRepository repository, SentimentAnalyzer sentimentAnalyzer) {
        super(repository, sentimentAnalyzer);
    }

    public Optional<WsjTradeArticleEntity> getConsumerArticleByIdWithSentimentScore(int id) {
        return getArticleByIdWithSentimentScore(id, WsjTradeArticleEntity::getArticleText); // Assuming there's a getArticleText method in WsjHousingArticleEntity
    }

    @Override
    public Optional<SentimentScore> getSentimentScore(int id) {

        Optional<SentimentScore> sentimentScore = super.getSentimentScore(
                id,
                WsjTradeArticleEntity::getArticleText,     // Method reference for getting text
                WsjTradeArticleEntity::getArticleLinksHref, // Method reference for getting the unique identifier
                WsjTradeArticleEntity::getPublishedDate,
                WsjTradeArticleEntity::getArticleLinks,
                Util::convertDate3,
                WsjTradeArticleEntity::getArticleType,
                WsjTradeArticleEntity::getArticleSource
        );
        return sentimentScore;
    }

    @Override
    public List<SentimentScore> getSentimentScoreForAll() {
        return super.getSentimentScoreForAll(WsjTradeArticleEntity::getArticleText,     // Method reference for getting text
                                             WsjTradeArticleEntity::getArticleLinksHref,
                                             WsjTradeArticleEntity::getPublishedDate,// Method reference for getting the unique identifier);
                                             WsjTradeArticleEntity::getArticleLinks,
                                             Util::convertDate3,
                                                WsjTradeArticleEntity::getArticleType,
                                                WsjTradeArticleEntity::getArticleSource
        );
    }

    public boolean checkArticleExists(String articleLinksHref) {
        return articleRepository.existsByArticleLinksHref(articleLinksHref);
    }
    public void cleansing() {
        this.updateDates();
        this.findPublishedDateWhenPublishedDateIsNullAndUpdateWithPublishedDate();
    }
    public void updateDates() {
        articleRepository.updateWsjPublishedDates();
    }

    public void findPublishedDateWhenPublishedDateIsNullAndUpdateWithPublishedDate() {
        List<WsjTradeArticleEntity> articles = articleRepository.findAll();
        articles.stream().forEach(article -> {
            if (article.getPublishedDate() == null || article.getPublishedDate().isEmpty()) {
                article.setPublishedDate(Util.extractPublishedDateFromWsj(article.getArticleText()));
                articleRepository.save(article);
            }
        });
    }
}
