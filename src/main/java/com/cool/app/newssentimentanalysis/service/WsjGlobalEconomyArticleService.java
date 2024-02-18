package com.cool.app.newssentimentanalysis.service;

import com.cool.app.newssentimentanalysis.controller.Util;
import com.cool.app.newssentimentanalysis.entity.SentimentScore;
import com.cool.app.newssentimentanalysis.entity.WsjGlobalEconomyArticleEntity;
import com.cool.app.newssentimentanalysis.repository.WsjGlobalEconomyArticleRepository;
import com.cool.app.newssentimentanalysis.sentiment.SentimentAnalyzer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WsjGlobalEconomyArticleService extends GenericArticleService<WsjGlobalEconomyArticleEntity, WsjGlobalEconomyArticleRepository> {
    @Autowired
    public WsjGlobalEconomyArticleService(WsjGlobalEconomyArticleRepository repository, SentimentAnalyzer sentimentAnalyzer) {
        super(repository, sentimentAnalyzer);
    }

    public Optional<WsjGlobalEconomyArticleEntity> getConsumerArticleByIdWithSentimentScore(int id) {
        return getArticleByIdWithSentimentScore(id, WsjGlobalEconomyArticleEntity::getArticleText); // Assuming there's a getArticleText method in WsjHousingArticleEntity
    }

    @Override
    public Optional<SentimentScore> getSentimentScore(int id) {

        Optional<SentimentScore> sentimentScore = super.getSentimentScore(
                id,
                WsjGlobalEconomyArticleEntity::getArticleText,     // Method reference for getting text
                WsjGlobalEconomyArticleEntity::getArticleLinksHref, // Method reference for getting the unique identifier
                WsjGlobalEconomyArticleEntity::getPublishedDate,
                WsjGlobalEconomyArticleEntity::getArticleLinks,
                Util::convertDate3,
                WsjGlobalEconomyArticleEntity::getArticleType,
                WsjGlobalEconomyArticleEntity::getArticleSource
        );
        return sentimentScore;
    }

    @Override
    public List<SentimentScore> getSentimentScoreForAll() {
        return super.getSentimentScoreForAll(WsjGlobalEconomyArticleEntity::getArticleText,     // Method reference for getting text
                                             WsjGlobalEconomyArticleEntity::getArticleLinksHref,
                                             WsjGlobalEconomyArticleEntity::getPublishedDate,// Method reference for getting the unique identifier);
                                             WsjGlobalEconomyArticleEntity::getArticleLinks,
                                             Util::convertDate3,
                                                WsjGlobalEconomyArticleEntity::getArticleType,
                                                WsjGlobalEconomyArticleEntity::getArticleSource
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
       List<WsjGlobalEconomyArticleEntity> articles = articleRepository.findAll();
         articles.stream().forEach(article -> {
              if (article.getPublishedDate() == null || article.getPublishedDate().isEmpty()) {
                article.setPublishedDate(Util.extractPublishedDateFromWsj(article.getArticleText()));
                articleRepository.save(article);
              }
         });
    }
}
