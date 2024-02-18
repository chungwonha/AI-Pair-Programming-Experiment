package com.cool.app.newssentimentanalysis.controller;

import com.cool.app.newssentimentanalysis.repository.WsjEarningsArticleRepository;
import com.cool.app.newssentimentanalysis.entity.SentimentScore;
import com.cool.app.newssentimentanalysis.entity.WsjEarningsArticleEntity;
import com.cool.app.newssentimentanalysis.service.WsjEarningsArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/wsj-earnings-articles")
public class WsjEarningsArticleController extends AbstractBaseCsvController<WsjEarningsArticleEntity, Integer, WsjEarningsArticleRepository>{
    @Value("${file.wsj.earnings.csv-folder-path}")
    private String wsjCsvFilePath;

    private final WsjEarningsArticleService wsjEarningsArticleService;

    @Autowired
    public WsjEarningsArticleController(WsjEarningsArticleService wsjEarningsArticleService) {
        this.wsjEarningsArticleService = wsjEarningsArticleService;
    }
    @Override
    protected Class<WsjEarningsArticleEntity> getEntityClass() {
        return WsjEarningsArticleEntity.class;
    }

    @Override
    protected String getUniqueAttributeValue(WsjEarningsArticleEntity entity) {
        return entity.getArticleLinksHref();
    }

    @Override
    protected boolean checkArticleExists(String housingArticleHeadHref) {
        return repository.existsByArticleLinksHref(housingArticleHeadHref);
    }

    @GetMapping("/load-csv")
    public String loadCsvData() {
        return super.loadCsvData(wsjCsvFilePath);
    }

    @PostMapping("/cleansing")
    public String cleansing() {
        wsjEarningsArticleService.cleansing();
        return "cleansing done";
    }
    @GetMapping("/get-article/{id}")
    public ResponseEntity<WsjEarningsArticleEntity> getArticleById(@PathVariable int id) {
        return this.wsjEarningsArticleService.getArticleById(id)
                .map(ResponseEntity::ok) // if article is found, return it with HTTP 200
                .orElse(ResponseEntity.notFound().build()); // if not found, return HTTP 404
    }

    @GetMapping("/execute-sentiment-analysis/{id}")
    public ResponseEntity<WsjEarningsArticleEntity> executeSentimentAnalysisOnArticleById(@PathVariable int id) {
        Optional<WsjEarningsArticleEntity> article = this.wsjEarningsArticleService.getEarningsArticleByIdWithSentimentScore(id);

        return article
                .map(a -> ResponseEntity.ok().body(a)) // Replace 'a' with how you want to return the article and its sentiment
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/get-sentiment-score/{id}")
    public ResponseEntity<SentimentScore> getSentimentScore(@PathVariable int id) {
        Optional<SentimentScore> article = wsjEarningsArticleService.getSentimentScore(id);
        return article
                .map(a -> ResponseEntity.ok().body(a)) // Replace 'a' with how you want to return the article and its sentiment
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/get-sentiment-score-for-all")
    public ResponseEntity<List<SentimentScore>> getSentimentScoreForAll() {
        List<SentimentScore> sentimentScores = wsjEarningsArticleService.getSentimentScoreForAll();

        if (sentimentScores.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(sentimentScores);
        }
    }

    @PostMapping ("/chat-with-openai-for-news-article-summary-and-save")
    public String chatWithOpenAIForNewsArticleSummaryAndSave() {
        wsjEarningsArticleService.chatWithOpenAIForNewsArticleSummaryAndSave();
        return "completed";
    }
    @PostMapping("/generateHtml")
    public String generateHtmlAndUpload() {
        wsjEarningsArticleService.generateHtmlAndUpload();
        return "html generated and uploaded";
    }
}
