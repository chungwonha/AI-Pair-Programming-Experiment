package com.cool.app.newssentimentanalysis.service;

import com.cool.app.newssentimentanalysis.controller.Util;
import com.cool.app.newssentimentanalysis.repository.WsjEarningsArticleRepository;
import com.cool.app.newssentimentanalysis.entity.SentimentScore;
import com.cool.app.newssentimentanalysis.entity.WsjEarningsArticleEntity;
import com.cool.app.newssentimentanalysis.sentiment.SentimentAnalyzer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Service
public class WsjEarningsArticleService extends GenericArticleService<WsjEarningsArticleEntity, WsjEarningsArticleRepository> {
    @Autowired
    public WsjEarningsArticleService(WsjEarningsArticleRepository repository, SentimentAnalyzer sentimentAnalyzer) {
        super(repository, sentimentAnalyzer);
    }

    public Optional<WsjEarningsArticleEntity> getEarningsArticleByIdWithSentimentScore(int id) {
        return getArticleByIdWithSentimentScore(id, WsjEarningsArticleEntity::getArticleText); // Assuming there's a getArticleText method in WsjHousingArticleEntity
    }

    @Override
    public Optional<SentimentScore> getSentimentScore(int id) {

        Optional<SentimentScore> sentimentScore = super.getSentimentScore(
                id,
                WsjEarningsArticleEntity::getArticleText,     // Method reference for getting text
                WsjEarningsArticleEntity::getArticleLinksHref, // Method reference for getting the unique identifier
                WsjEarningsArticleEntity::getPublishedDate,
                WsjEarningsArticleEntity::getArticleLinks,
                Util::convertDate3,
                WsjEarningsArticleEntity::getArticleType,
                WsjEarningsArticleEntity::getArticleSource
        );
        return sentimentScore;
    }

    @Override
    public List<SentimentScore> getSentimentScoreForAll() {
        return super.getSentimentScoreForAll(WsjEarningsArticleEntity::getArticleText,     // Method reference for getting text
                                             WsjEarningsArticleEntity::getArticleLinksHref,
                                             WsjEarningsArticleEntity::getPublishedDate,// Method reference for getting the unique identifier);
                                             WsjEarningsArticleEntity::getArticleLinks,
                                             Util::convertDate3,
                                                WsjEarningsArticleEntity::getArticleType,
                                                WsjEarningsArticleEntity::getArticleSource
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
       List<WsjEarningsArticleEntity> articles = articleRepository.findAll();
       articles.stream().forEach(article -> {
           if (article.getPublishedDate() == null || article.getPublishedDate().isEmpty()) {
               article.setPublishedDate(Util.extractPublishedDateFromWsj(article.getArticleText()));
               articleRepository.save(article);
           }
       });
    }

    public void chatWithOpenAIForNewsArticleSummaryAndSave(){
        super.chatWithOpenAIForNewsArticleSummaryAndSave(WsjEarningsArticleEntity::getArticleText,
                                                         WsjEarningsArticleEntity::getArticleLinksHref);
    }

    public void generateHtmlAndUpload() {
        Function<String, String> findArticleTitleByArticleLinksHref = (articleLinksHref) -> articleRepository.findArticleTitleByArticleLinksHref(articleLinksHref);
        super.generateHtmlFromJsonAndUploadToS3(findArticleTitleByArticleLinksHref,
                                                 WsjEarningsArticleEntity::getArticleLinksHref);
    }
}
