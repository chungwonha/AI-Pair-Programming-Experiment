package com.cool.app.newssentimentanalysis.controller;

import com.cool.app.newssentimentanalysis.service.SentimentAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BackgroundTaskController {

    private final SentimentAnalysisService sentimentAnalysisService;

    @Autowired
    public BackgroundTaskController(SentimentAnalysisService sentimentAnalysisService) {
        this.sentimentAnalysisService = sentimentAnalysisService;
    }

    @GetMapping("/run-sentiment-analysis")
    public ResponseEntity<String> runSentimentAnalysis() {
        sentimentAnalysisService.runSentimentAnalysis();
        return ResponseEntity.accepted().body("Sentiment analysis started in the background");
    }
}

