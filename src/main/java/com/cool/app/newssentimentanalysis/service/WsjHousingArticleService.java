package com.cool.app.newssentimentanalysis.service;

import com.cool.app.newssentimentanalysis.controller.Util;
import com.cool.app.newssentimentanalysis.entity.SentimentScore;
import com.cool.app.newssentimentanalysis.entity.WsjHousingArticleEntity;
import com.cool.app.newssentimentanalysis.repository.WsjHousingArticleRepository;
import com.cool.app.newssentimentanalysis.sentiment.SentimentAnalyzer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WsjHousingArticleService extends GenericArticleService<WsjHousingArticleEntity, WsjHousingArticleRepository> {

    @Autowired
    public WsjHousingArticleService(WsjHousingArticleRepository repository, SentimentAnalyzer sentimentAnalyzer) {
        super(repository, sentimentAnalyzer);
    }

    public Optional<WsjHousingArticleEntity> getHousingArticleByIdWithSentimentScore(int id) {
        return getArticleByIdWithSentimentScore(id, WsjHousingArticleEntity::getArticleText); // Assuming there's a getArticleText method in WsjHousingArticleEntity
    }

    @Override
    public Optional<SentimentScore> getSentimentScore(int id) {

        Optional<SentimentScore> sentimentScore = super.getSentimentScore(
                id,
                WsjHousingArticleEntity::getArticleText,     // Method reference for getting text
                WsjHousingArticleEntity::getHousingArticleHeadHref, // Method reference for getting the unique identifier
                WsjHousingArticleEntity::getPublishedDate,
                WsjHousingArticleEntity::getHousingArticleHead,
                Util::convertDate3,
                WsjHousingArticleEntity::getArticleType,
                WsjHousingArticleEntity::getArticleSource
        );
        return sentimentScore;
    }

    @Override
    public List<SentimentScore> getSentimentScoreForAll() {
        return super.getSentimentScoreForAll(WsjHousingArticleEntity::getArticleText,     // Method reference for getting text
                                             WsjHousingArticleEntity::getHousingArticleHeadHref,
                                             WsjHousingArticleEntity::getPublishedDate,// Method reference for getting the unique identifier);
                                             WsjHousingArticleEntity::getHousingArticleHead,
                                             Util::convertDate3,
                                                WsjHousingArticleEntity::getArticleType,
                                                WsjHousingArticleEntity::getArticleSource
        );
    }

    public boolean checkHousingArticleExists(String housingArticleHeadHref) {
        return articleRepository.existsByHousingArticleHeadHref(housingArticleHeadHref);
    }

    public void cleansing() {
        this.updateDates();
        this.findPublishedDateWhenPublishedDateIsNullAndUpdateWithPublishedDate();
    }
    public void updateDates() {
        articleRepository.updateWsjPublishedDates();
    }

    public void findPublishedDateWhenPublishedDateIsNullAndUpdateWithPublishedDate() {
        List<WsjHousingArticleEntity> articles = articleRepository.findAll();
        articles.stream().forEach(article -> {
            if (article.getPublishedDate() == null || article.getPublishedDate().isEmpty()) {
                article.setPublishedDate(Util.extractPublishedDateFromWsj(article.getArticleText()));
                articleRepository.save(article);
            }
        });
    }
}
