package com.cool.app.newssentimentanalysis.service;

import com.cool.app.newssentimentanalysis.controller.Util;
import com.cool.app.newssentimentanalysis.entity.SentimentScore;
import com.cool.app.newssentimentanalysis.entity.WsjJobArticleEntity;
import com.cool.app.newssentimentanalysis.repository.WsjJobArticleRepository;
import com.cool.app.newssentimentanalysis.sentiment.SentimentAnalyzer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WsjJobArticleService extends GenericArticleService<WsjJobArticleEntity, WsjJobArticleRepository>{

    @Autowired
    public WsjJobArticleService(WsjJobArticleRepository repository, SentimentAnalyzer sentimentAnalyzer) {
        super(repository, sentimentAnalyzer);
    }

    public Optional<WsjJobArticleEntity> getJobArticleByIdWithSentimentScore(int id) {
        return getArticleByIdWithSentimentScore(id, WsjJobArticleEntity::getArticleText); // Assuming there's a getArticleText method in WsjJobArticleEntity
    }

    @Override
    public Optional<SentimentScore> getSentimentScore(int id) {

        Optional<SentimentScore> sentimentScore = super.getSentimentScore(
                id,
                WsjJobArticleEntity::getArticleText,     // Method reference for getting text
                WsjJobArticleEntity::getJobArticleHeadHref, // Method reference for getting the unique identifier
                WsjJobArticleEntity::getPublishedDate,
                WsjJobArticleEntity::getJobArticleHead,
                Util::convertDate3,
                WsjJobArticleEntity::getArticleType,
                WsjJobArticleEntity::getArticleSource
        );
        return sentimentScore;
    }

    @Override
    public List<SentimentScore> getSentimentScoreForAll(){
        return super.getSentimentScoreForAll( WsjJobArticleEntity::getArticleText,     // Method reference for getting text
                                              WsjJobArticleEntity::getJobArticleHeadHref,
                                              WsjJobArticleEntity::getPublishedDate,// Method reference for getting the unique identifier
                                              WsjJobArticleEntity::getJobArticleHead,
                                              Util::convertDate3,
                                                    WsjJobArticleEntity::getArticleType,
                                                    WsjJobArticleEntity::getArticleSource
                 );

    }

    public boolean checkArticleExists(String jobArticleHeadHref) {
        return articleRepository.existsByJobArticleHeadHref(jobArticleHeadHref);
    }

    public void cleansing() {
        this.updateDates();
        this.findPublishedDateWhenPublishedDateIsNullAndUpdateWithPublishedDate();
    }
    public void updateDates() {
        articleRepository.updateWsjPublishedDates();
    }
    public void findPublishedDateWhenPublishedDateIsNullAndUpdateWithPublishedDate() {
        List<WsjJobArticleEntity> articles = articleRepository.findAll();
        articles.stream().forEach(article -> {
            if (article.getPublishedDate() == null || article.getPublishedDate().isEmpty()) {
                article.setPublishedDate(Util.extractPublishedDateFromWsj(article.getArticleText()));
                articleRepository.save(article);
            }
        });
    }
}
