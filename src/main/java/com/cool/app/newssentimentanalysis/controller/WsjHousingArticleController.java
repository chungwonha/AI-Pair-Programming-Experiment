package com.cool.app.newssentimentanalysis.controller;

import com.cool.app.newssentimentanalysis.repository.WsjHousingArticleRepository;

import com.cool.app.newssentimentanalysis.entity.SentimentScore;
import com.cool.app.newssentimentanalysis.entity.WsjHousingArticleEntity;
import com.cool.app.newssentimentanalysis.service.WsjHousingArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/wsj-housing-articles")
public class WsjHousingArticleController extends AbstractBaseCsvController<WsjHousingArticleEntity, Integer, WsjHousingArticleRepository>{
    @Value("${file.wsj.housing.csv-folder-path}")
    private String wsjCsvFilePath;

    private final WsjHousingArticleService wsjHousingArticleService;

    @Autowired
    public WsjHousingArticleController(WsjHousingArticleService wsjHousingArticleService) {
        this.wsjHousingArticleService = wsjHousingArticleService;
    }
    @Override
    protected Class<WsjHousingArticleEntity> getEntityClass() {
        return WsjHousingArticleEntity.class;
    }

    @Override
    protected String getUniqueAttributeValue(WsjHousingArticleEntity entity) {
        return entity.getHousingArticleHeadHref();
    }

    @Override
    protected boolean checkArticleExists(String housingArticleHeadHref) {
        return repository.existsByHousingArticleHeadHref(housingArticleHeadHref);
    }

    @GetMapping("/load-csv")
    public String loadCsvData() {
        return super.loadCsvData(wsjCsvFilePath);
    }

    @PostMapping("/cleansing")
    public String cleansing() {
        wsjHousingArticleService.cleansing();
        return "cleansing done";
    }
    @GetMapping("/get-article/{id}")
    public ResponseEntity<WsjHousingArticleEntity> getArticleById(@PathVariable int id) {
        return wsjHousingArticleService.getArticleById(id)
                .map(ResponseEntity::ok) // if article is found, return it with HTTP 200
                .orElse(ResponseEntity.notFound().build()); // if not found, return HTTP 404
    }

    @GetMapping("/execute-sentiment-analysis/{id}")
    public ResponseEntity<WsjHousingArticleEntity> executeSentimentAnalysisOnArticleById(@PathVariable int id) {
        Optional<WsjHousingArticleEntity> article = wsjHousingArticleService.getHousingArticleByIdWithSentimentScore(id);

        return article
                .map(a -> ResponseEntity.ok().body(a)) // Replace 'a' with how you want to return the article and its sentiment
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    @GetMapping("/get-sentiment-score/{id}")
    public ResponseEntity<SentimentScore> getSentimentScore(@PathVariable int id) {
        Optional<SentimentScore> article = wsjHousingArticleService.getSentimentScore(id);
        return article
                .map(a -> ResponseEntity.ok().body(a)) // Replace 'a' with how you want to return the article and its sentiment
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/get-sentiment-score-for-all")
    public ResponseEntity<List<SentimentScore>> getSentimentScoreForAll() {
        List<SentimentScore> sentimentScores = wsjHousingArticleService.getSentimentScoreForAll();

        if (sentimentScores.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(sentimentScores);
        }
    }

}
