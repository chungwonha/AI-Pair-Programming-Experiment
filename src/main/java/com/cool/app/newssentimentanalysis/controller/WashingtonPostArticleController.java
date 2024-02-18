package com.cool.app.newssentimentanalysis.controller;

import com.cool.app.newssentimentanalysis.repository.WashingtonPostEconomyArticleRepository;
import com.cool.app.newssentimentanalysis.entity.SentimentScore;
import com.cool.app.newssentimentanalysis.entity.WashingtonPostEconomyArticleEntity;
import com.cool.app.newssentimentanalysis.service.WashingtonPostEconomyArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/wash-post-econo-articles")
public class WashingtonPostArticleController extends AbstractBaseCsvController<WashingtonPostEconomyArticleEntity, Integer, WashingtonPostEconomyArticleRepository>{

    @Value("${file.wpe.csv-folder-path}")
    private String wpeCsvFilePath;

    private final WashingtonPostEconomyArticleService washingtonPostEconomyArticleService;

    @Autowired
    public WashingtonPostArticleController(WashingtonPostEconomyArticleService washingtonPostEconomyArticleService) {
        this.washingtonPostEconomyArticleService = washingtonPostEconomyArticleService;
    }
    @Override
    protected Class<WashingtonPostEconomyArticleEntity> getEntityClass() {
        return WashingtonPostEconomyArticleEntity.class;
    }

    @Override
    protected String getUniqueAttributeValue(WashingtonPostEconomyArticleEntity entity) {
        return entity.getArticleLinksHref();
    }

    @Override
    protected boolean checkArticleExists(String articleLinksHref) {
        return repository.existsByArticleLinksHref(articleLinksHref);
    }

    @GetMapping("/load-csv")
    public String loadCsvData() {
        return super.loadCsvData(wpeCsvFilePath);
    }

    @PostMapping("/cleansing")
    public String cleansing() {
        washingtonPostEconomyArticleService.cleansing();
        return "cleansing done";
    }
    @GetMapping("/get-article/{id}")
    public ResponseEntity<WashingtonPostEconomyArticleEntity> getArticleById(@PathVariable int id) {
        return washingtonPostEconomyArticleService.getArticleById(id)
                .map(ResponseEntity::ok) // if article is found, return it with HTTP 200
                .orElse(ResponseEntity.notFound().build()); // if not found, return HTTP 404
    }

    @GetMapping("/get-sentiment-score/{id}")
    public ResponseEntity<SentimentScore> getSentimentScore(@PathVariable int id) {
        Optional<SentimentScore> article = washingtonPostEconomyArticleService.getSentimentScore(id);
        return article
                .map(a -> ResponseEntity.ok().body(a)) // Replace 'a' with how you want to return the article and its sentiment
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @GetMapping("/get-sentiment-score-for-all")
    public ResponseEntity<List<SentimentScore>> getSentimentScoreForAll() {
        List<SentimentScore> sentimentScores = washingtonPostEconomyArticleService.getSentimentScoreForAll();

        if (sentimentScores.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(sentimentScores);
        }
    }
}
