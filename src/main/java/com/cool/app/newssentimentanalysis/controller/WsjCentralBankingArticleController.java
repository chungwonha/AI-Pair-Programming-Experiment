package com.cool.app.newssentimentanalysis.controller;

import com.cool.app.newssentimentanalysis.entity.SentimentScore;
import com.cool.app.newssentimentanalysis.entity.WsjCentralBankingArticleEntity;
import com.cool.app.newssentimentanalysis.entity.WsjGlobalEconomyArticleEntity;
import com.cool.app.newssentimentanalysis.repository.WsjCentralBankingArticleRepository;
import com.cool.app.newssentimentanalysis.repository.WsjGlobalEconomyArticleRepository;
import com.cool.app.newssentimentanalysis.service.WsjCentralBankingArticleService;
import com.cool.app.newssentimentanalysis.service.WsjGlobalEconomyArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/wsj-central-banking-articles")
public class WsjCentralBankingArticleController extends AbstractBaseCsvController<WsjCentralBankingArticleEntity, Integer, WsjCentralBankingArticleRepository>{
    @Value("${file.wsj.central-banking.csv-folder-path}")
    private String wsjCsvFilePath;

    private final WsjCentralBankingArticleService wsjCentralBankingArticleService;

    @Autowired
    public WsjCentralBankingArticleController(WsjCentralBankingArticleService wsjCentralBankingArticleService) {
        this.wsjCentralBankingArticleService = wsjCentralBankingArticleService;
    }
    @Override
    protected Class<WsjCentralBankingArticleEntity> getEntityClass() {
        return WsjCentralBankingArticleEntity.class;
    }



    @Override
    protected String getUniqueAttributeValue(WsjCentralBankingArticleEntity entity) {
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
        wsjCentralBankingArticleService.cleansing();
        return "cleansing done";
    }
    @GetMapping("/get-article/{id}")
    public ResponseEntity<WsjCentralBankingArticleEntity> getArticleById(@PathVariable int id) {
        return wsjCentralBankingArticleService.getArticleById(id)
                .map(ResponseEntity::ok) // if article is found, return it with HTTP 200
                .orElse(ResponseEntity.notFound().build()); // if not found, return HTTP 404
    }

    @GetMapping("/execute-sentiment-analysis/{id}")
    public ResponseEntity<WsjCentralBankingArticleEntity> executeSentimentAnalysisOnArticleById(@PathVariable int id) {
        Optional<WsjCentralBankingArticleEntity> article = wsjCentralBankingArticleService.getConsumerArticleByIdWithSentimentScore(id);

        return article
                .map(a -> ResponseEntity.ok().body(a)) // Replace 'a' with how you want to return the article and its sentiment
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/get-sentiment-score/{id}")
    public ResponseEntity<SentimentScore> getSentimentScore(@PathVariable int id) {
        Optional<SentimentScore> article = wsjCentralBankingArticleService.getSentimentScore(id);
        return article
                .map(a -> ResponseEntity.ok().body(a)) // Replace 'a' with how you want to return the article and its sentiment
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/get-sentiment-score-for-all")
    public ResponseEntity<List<SentimentScore>> getSentimentScoreForAll() {
        List<SentimentScore> sentimentScores = wsjCentralBankingArticleService.getSentimentScoreForAll();

        if (sentimentScores.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(sentimentScores);
        }
    }

    @PostMapping ("/chat-with-openai-for-news-article-summary-and-save")
    public String chatWithOpenAIForNewsArticleSummaryAndSave() {
        wsjCentralBankingArticleService.chatWithOpenAIForNewsArticleSummaryAndSave();
        return "completed";
    }

    @PostMapping("/generateHtml")
    public String generateHtmlAndUpload() {
        wsjCentralBankingArticleService.generateHtmlAndUpload();
        return "html generated and uploaded";
    }


}
