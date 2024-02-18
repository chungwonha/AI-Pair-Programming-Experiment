package com.cool.app.newssentimentanalysis.controller;

import com.cool.app.newssentimentanalysis.entity.SentimentScore;
import com.cool.app.newssentimentanalysis.entity.WsjTradeArticleEntity;
import com.cool.app.newssentimentanalysis.repository.WsjTradeArticleRepository;
import com.cool.app.newssentimentanalysis.service.WsjTradeArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/wsj-trade-articles")
public class WsjTradeArticleController extends AbstractBaseCsvController<WsjTradeArticleEntity, Integer, WsjTradeArticleRepository>{
    @Value("${file.wsj.trade.csv-folder-path}")
    private String wsjCsvFilePath;

    private final WsjTradeArticleService wsjTradeArticleService;

    @Autowired
    public WsjTradeArticleController(WsjTradeArticleService wsjTradeArticleService) {
        this.wsjTradeArticleService = wsjTradeArticleService;
    }
    @Override
    protected Class<WsjTradeArticleEntity> getEntityClass() {
        return WsjTradeArticleEntity.class;
    }



    @Override
    protected String getUniqueAttributeValue(WsjTradeArticleEntity entity) {
        return entity.getArticleLinksHref();
    }

    @Override
    protected boolean checkArticleExists(String articleHeadHref) {
        return repository.existsByArticleLinksHref(articleHeadHref);
    }

    @GetMapping("/load-csv")
    public String loadCsvData() {
        return super.loadCsvData(wsjCsvFilePath);
    }

    @PostMapping("/cleansing")
    public String cleansing() {
        wsjTradeArticleService.cleansing();
        return "cleansing done";
    }

    @GetMapping("/get-article/{id}")
    public ResponseEntity<WsjTradeArticleEntity> getArticleById(@PathVariable int id) {
        return wsjTradeArticleService.getArticleById(id)
                .map(ResponseEntity::ok) // if article is found, return it with HTTP 200
                .orElse(ResponseEntity.notFound().build()); // if not found, return HTTP 404
    }

    @GetMapping("/execute-sentiment-analysis/{id}")
    public ResponseEntity<WsjTradeArticleEntity> executeSentimentAnalysisOnArticleById(@PathVariable int id) {
        Optional<WsjTradeArticleEntity> article = wsjTradeArticleService.getConsumerArticleByIdWithSentimentScore(id);

        return article
                .map(a -> ResponseEntity.ok().body(a)) // Replace 'a' with how you want to return the article and its sentiment
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/get-sentiment-score/{id}")
    public ResponseEntity<SentimentScore> getSentimentScore(@PathVariable int id) {
        Optional<SentimentScore> article = wsjTradeArticleService.getSentimentScore(id);
        return article
                .map(a -> ResponseEntity.ok().body(a)) // Replace 'a' with how you want to return the article and its sentiment
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/get-sentiment-score-for-all")
    public ResponseEntity<List<SentimentScore>> getSentimentScoreForAll() {
        List<SentimentScore> sentimentScores = wsjTradeArticleService.getSentimentScoreForAll();

        if (sentimentScores.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(sentimentScores);
        }
    }
}
