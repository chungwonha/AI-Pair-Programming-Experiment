package com.cool.app.newssentimentanalysis.service;

import com.cool.app.newssentimentanalysis.controller.Util;
import com.cool.app.newssentimentanalysis.entity.SentimentScore;
import com.cool.app.newssentimentanalysis.entity.WsjCentralBankingArticleEntity;
import com.cool.app.newssentimentanalysis.repository.WsjCentralBankingArticleRepository;
import com.cool.app.newssentimentanalysis.sentiment.SentimentAnalyzer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Service
public class WsjCentralBankingArticleService extends GenericArticleService<WsjCentralBankingArticleEntity, WsjCentralBankingArticleRepository> {

    private final WsjCentralBankingArticleRepository articleRepository;
    @Autowired
    public WsjCentralBankingArticleService(WsjCentralBankingArticleRepository repository, SentimentAnalyzer sentimentAnalyzer) {
                super(repository, sentimentAnalyzer);
                articleRepository = repository;
    }

    public Optional<WsjCentralBankingArticleEntity> getConsumerArticleByIdWithSentimentScore(int id) {
        return getArticleByIdWithSentimentScore(id, WsjCentralBankingArticleEntity::getArticleText); // Assuming there's a getArticleText method in WsjHousingArticleEntity
    }

    @Override
    public Optional<SentimentScore> getSentimentScore(int id) {

        Optional<SentimentScore> sentimentScore = super.getSentimentScore(
                id,
                WsjCentralBankingArticleEntity::getArticleText,     // Method reference for getting text
                WsjCentralBankingArticleEntity::getArticleLinksHref, // Method reference for getting the unique identifier
                WsjCentralBankingArticleEntity::getPublishedDate,
                WsjCentralBankingArticleEntity::getArticleLinks,
                Util::convertDate3,
                WsjCentralBankingArticleEntity::getArticleType,
                WsjCentralBankingArticleEntity::getArticleSource
        );
        return sentimentScore;
    }

    @Override
    public List<SentimentScore> getSentimentScoreForAll() {
        return super.getSentimentScoreForAll(WsjCentralBankingArticleEntity::getArticleText,     // Method reference for getting text
                                             WsjCentralBankingArticleEntity::getArticleLinksHref, // Method reference for getting the unique identifier);
                                             WsjCentralBankingArticleEntity::getPublishedDate,
                                             WsjCentralBankingArticleEntity::getArticleLinks,
                                             Util::convertDate3,
                                             WsjCentralBankingArticleEntity::getArticleType,
                                             WsjCentralBankingArticleEntity::getArticleSource
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
        List<WsjCentralBankingArticleEntity> articles = articleRepository.findAll();
        articles.stream().forEach(article -> {
            if (article.getPublishedDate() == null || article.getPublishedDate().isEmpty()) {
                String publishedDate = Util.extractPublishedDateFromWsj(article.getArticleText());
                article.setPublishedDate(publishedDate);
                articleRepository.save(article);
            }
        });
    }

    public void chatWithOpenAIForNewsArticleSummaryAndSave(){

        super.chatWithOpenAIForNewsArticleSummaryAndSave(WsjCentralBankingArticleEntity::getArticleText,
                                                         WsjCentralBankingArticleEntity::getArticleLinksHref);

    }

    public void generateHtmlAndUpload() {
        logger.info("generateHtmlAndUpload");
//        Function<Integer, WsjCentralBankingArticleEntity> findWsjCentralBankingArticleEntityIdById = (id) -> articleRepository.findById(id).get();
        Function<String, String> findArticleTitleByArticleLinksHref = (articleLinksHref) -> articleRepository.findArticleTitleByArticleLinksHref(articleLinksHref);
        super.generateHtmlFromJsonAndUploadToS3(findArticleTitleByArticleLinksHref,
                                                WsjCentralBankingArticleEntity::getArticleLinks);
    }
}
