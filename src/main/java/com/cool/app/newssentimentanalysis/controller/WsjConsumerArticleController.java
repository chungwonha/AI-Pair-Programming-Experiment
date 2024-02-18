package com.cool.app.newssentimentanalysis.controller;

import com.cool.app.newssentimentanalysis.repository.WsjConsumerArticleRepository;
import com.cool.app.newssentimentanalysis.entity.SentimentScore;
import com.cool.app.newssentimentanalysis.entity.WsjConsumerArticleEntity;
import com.cool.app.newssentimentanalysis.service.WsjConsumerArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/wsj-consumer-articles")
public class WsjConsumerArticleController extends AbstractBaseCsvController<WsjConsumerArticleEntity, Integer, WsjConsumerArticleRepository>{
    @Value("${file.wsj.consumer.csv-folder-path}")
    private String wsjCsvFilePath;

    private final WsjConsumerArticleService wsjConsumerArticleService;

    @Autowired
    public WsjConsumerArticleController(WsjConsumerArticleService wsjConsumerArticleService) {
        this.wsjConsumerArticleService = wsjConsumerArticleService;
    }
    @Override
    protected Class<WsjConsumerArticleEntity> getEntityClass() {
        return WsjConsumerArticleEntity.class;
    }

    @Override
    protected String getUniqueAttributeValue(WsjConsumerArticleEntity entity) {
        return entity.getArticleHeadHref();
    }

    @Override
    protected boolean checkArticleExists(String housingArticleHeadHref) {
        return repository.existsByArticleHeadHref(housingArticleHeadHref);
    }

    @GetMapping("/load-csv")
    public String loadCsvData() {
        return super.loadCsvData(wsjCsvFilePath);
    }

    @PostMapping("/cleansing")
    public String cleansing() {
        wsjConsumerArticleService.cleansing();
        return "cleansing done";
    }
    @GetMapping("/get-article/{id}")
    public ResponseEntity<WsjConsumerArticleEntity> getArticleById(@PathVariable int id) {
        return wsjConsumerArticleService.getArticleById(id)
                .map(ResponseEntity::ok) // if article is found, return it with HTTP 200
                .orElse(ResponseEntity.notFound().build()); // if not found, return HTTP 404
    }

    @GetMapping("/execute-sentiment-analysis/{id}")
    public ResponseEntity<WsjConsumerArticleEntity> executeSentimentAnalysisOnArticleById(@PathVariable int id) {
        Optional<WsjConsumerArticleEntity> article = wsjConsumerArticleService.getConsumerArticleByIdWithSentimentScore(id);

        return article
                .map(a -> ResponseEntity.ok().body(a)) // Replace 'a' with how you want to return the article and its sentiment
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/get-sentiment-score/{id}")
    public ResponseEntity<SentimentScore> getSentimentScore(@PathVariable int id) {
        Optional<SentimentScore> article = wsjConsumerArticleService.getSentimentScore(id);
        return article
                .map(a -> ResponseEntity.ok().body(a)) // Replace 'a' with how you want to return the article and its sentiment
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/get-sentiment-score-for-all")
    public ResponseEntity<List<SentimentScore>> getSentimentScoreForAll() {
        List<SentimentScore> sentimentScores = wsjConsumerArticleService.getSentimentScoreForAll();

        if (sentimentScores.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(sentimentScores);
        }
    }

    @PostMapping ("/chat-with-openai-for-news-article-summary-and-save")
    public String chatWithOpenAIForNewsArticleSummaryAndSave() {
        wsjConsumerArticleService.chatWithOpenAIForNewsArticleSummaryAndSave();
        return "completed";
    }
    @PostMapping("/generateHtml")
    public String generateHtmlAndUpload() {
        wsjConsumerArticleService.generateHtmlAndUpload();
        return "html generated and uploaded";
    }

}
