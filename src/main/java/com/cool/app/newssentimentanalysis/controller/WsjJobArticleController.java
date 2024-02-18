package com.cool.app.newssentimentanalysis.controller;

import com.cool.app.newssentimentanalysis.entity.SentimentScore;
import com.cool.app.newssentimentanalysis.entity.WsjJobArticleEntity;
import com.cool.app.newssentimentanalysis.repository.WsjJobArticleRepository;
import com.cool.app.newssentimentanalysis.service.WsjJobArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/wsj-job-articles")
public class WsjJobArticleController extends AbstractBaseCsvController<WsjJobArticleEntity, Integer, WsjJobArticleRepository> {

//    @Value("${file.wsj.csv.path}")

    @Value("${file.wsj.job.csv-folder-path}")
    private String wsjCsvFilePath;

    private final WsjJobArticleService wsjJobArticleService;

    @Autowired
    public WsjJobArticleController(WsjJobArticleService wsjJobArticleService) {
        this.wsjJobArticleService = wsjJobArticleService;
    }

    @Override
    protected Class<WsjJobArticleEntity> getEntityClass() {
        return WsjJobArticleEntity.class;
    }

    @Override
    protected String getUniqueAttributeValue(WsjJobArticleEntity entity) {
        return entity.getJobArticleHeadHref();
    }

    @Override
    protected boolean checkArticleExists(String jobArticleHeadHref) {
        return repository.existsByJobArticleHeadHref(jobArticleHeadHref);
    }
    @GetMapping("/load-csv")
    public String loadCsvData() {
        return super.loadCsvData(wsjCsvFilePath);
    }

    @PostMapping("/cleansing")
    public String cleansing() {
        wsjJobArticleService.cleansing();
        return "cleansing done";
    }
    @GetMapping("/get-article/{id}")
    public ResponseEntity<WsjJobArticleEntity> getArticleById(@PathVariable int id) {
        return wsjJobArticleService.getArticleById(id)
                .map(ResponseEntity::ok) // if article is found, return it with HTTP 200
                .orElse(ResponseEntity.notFound().build()); // if not found, return HTTP 404
    }

    @GetMapping("/execute-sentiment-analysis/{id}")
    public ResponseEntity<WsjJobArticleEntity> executeSentimentAnalysisOnArticleById(@PathVariable int id) {
        Optional<WsjJobArticleEntity> article = wsjJobArticleService.getJobArticleByIdWithSentimentScore(id);

        return article
                .map(a -> ResponseEntity.ok().body(a)) // Replace 'a' with how you want to return the article and its sentiment
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/get-sentiment-score/{id}")
    public ResponseEntity<SentimentScore> getSentimentScore(@PathVariable int id) {
        Optional<SentimentScore> article = wsjJobArticleService.getSentimentScore(id);
        return article
                .map(a -> ResponseEntity.ok().body(a)) // Replace 'a' with how you want to return the article and its sentiment
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/get-sentiment-score-for-all")
    public ResponseEntity<List<SentimentScore>> getSentimentScoreForAll() {
        List<SentimentScore> sentimentScores = wsjJobArticleService.getSentimentScoreForAll();

        if (sentimentScores.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(sentimentScores);
        }
    }

}
