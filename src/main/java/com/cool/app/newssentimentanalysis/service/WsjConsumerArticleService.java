package com.cool.app.newssentimentanalysis.service;

import com.cool.app.newssentimentanalysis.controller.Util;
import com.cool.app.newssentimentanalysis.entity.WsjCentralBankingArticleEntity;
import com.cool.app.newssentimentanalysis.repository.WsjConsumerArticleRepository;
import com.cool.app.newssentimentanalysis.entity.SentimentScore;
import com.cool.app.newssentimentanalysis.entity.WsjConsumerArticleEntity;
import com.cool.app.newssentimentanalysis.sentiment.SentimentAnalyzer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Service
public class WsjConsumerArticleService extends GenericArticleService<WsjConsumerArticleEntity, WsjConsumerArticleRepository> {
    @Autowired
    public WsjConsumerArticleService(WsjConsumerArticleRepository repository, SentimentAnalyzer sentimentAnalyzer) {
        super(repository, sentimentAnalyzer);
    }

    public Optional<WsjConsumerArticleEntity> getConsumerArticleByIdWithSentimentScore(int id) {
        return getArticleByIdWithSentimentScore(id, WsjConsumerArticleEntity::getArticleText); // Assuming there's a getArticleText method in WsjHousingArticleEntity
    }

    @Override
    public Optional<SentimentScore> getSentimentScore(int id) {

        Optional<SentimentScore> sentimentScore = super.getSentimentScore(
                id,
                WsjConsumerArticleEntity::getArticleText,     // Method reference for getting text
                WsjConsumerArticleEntity::getArticleHeadHref, // Method reference for getting the unique identifier
                WsjConsumerArticleEntity::getPublishedDate,
                WsjConsumerArticleEntity::getArticleHead,
                Util::convertDate3,
                WsjConsumerArticleEntity::getArticleType,
                WsjConsumerArticleEntity::getArticleSource
        );
        return sentimentScore;
    }

    @Override
    public List<SentimentScore> getSentimentScoreForAll() {
        return super.getSentimentScoreForAll(WsjConsumerArticleEntity::getArticleText,     // Method reference for getting text
                                             WsjConsumerArticleEntity::getArticleHeadHref,
                                             WsjConsumerArticleEntity::getPublishedDate,// Method reference for getting the unique identifier
                                             WsjConsumerArticleEntity::getArticleHead,
                                             Util::convertDate3,
                                                WsjConsumerArticleEntity::getArticleType,
                                                WsjConsumerArticleEntity::getArticleSource  // Method reference for getting the unique identifier
        );
    }

    public boolean checkArticleExists(String articleHeadHref) {
        return articleRepository.existsByArticleHeadHref(articleHeadHref);
    }

    public void cleansing() {
        this.updateDates();
        this.findPublishedDateWhenPublishedDateIsNullAndUpdateWithPublishedDate();
    }
    public void updateDates() {
        articleRepository.updateWsjPublishedDates();
    }

    public void findPublishedDateWhenPublishedDateIsNullAndUpdateWithPublishedDate() {
          List<WsjConsumerArticleEntity> wsjConsumerArticleServiceList = articleRepository.findAll();
          wsjConsumerArticleServiceList.stream().forEach(wsjConsumerArticleEntity -> {
              if (wsjConsumerArticleEntity.getPublishedDate() == null || wsjConsumerArticleEntity.getPublishedDate().isEmpty()) {
                  String publishedDate = Util.extractPublishedDateFromWsj(wsjConsumerArticleEntity.getArticleText());
                  articleRepository.updatePublishedDateById(wsjConsumerArticleEntity.getId(), publishedDate);
              }
          });
    }

    public void chatWithOpenAIForNewsArticleSummaryAndSave(){
        super.chatWithOpenAIForNewsArticleSummaryAndSave(WsjConsumerArticleEntity::getArticleText,
                                                         WsjConsumerArticleEntity::getArticleHeadHref);
    }
    public void generateHtmlAndUpload() {
        logger.info("generateHtmlAndUpload");

        Function<String, String> findArticleHeadByArticleLinksHref = (articleLinksHref) -> articleRepository.findArticleHeadByArticleHeadHref(articleLinksHref);
        super.generateHtmlFromJsonAndUploadToS3(findArticleHeadByArticleLinksHref,
                                                WsjConsumerArticleEntity::getArticleHeadHref);
    }
}
