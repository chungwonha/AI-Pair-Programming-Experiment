package com.cool.app.newssentimentanalysis.controller;

import com.cool.app.newssentimentanalysis.entity.SentimentScore;
import com.cool.app.newssentimentanalysis.service.SentimentScoreService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/sentiment-scores")
@Slf4j
public class SentimentScoreController {
    private static final Logger logger = LoggerFactory.getLogger(SentimentScoreController.class);

    private final SentimentScoreService sentimentScoreService;
    private final RestTemplate restTemplate;

    @Autowired
    public SentimentScoreController(SentimentScoreService sentimentScoreService, RestTemplate restTemplate) {
        this.sentimentScoreService = sentimentScoreService;
        this.restTemplate = restTemplate;
    }

    @GetMapping("/{id}")
    public ResponseEntity<SentimentScore> getSentimentScore(@PathVariable Long id) {
        Optional<SentimentScore> sentimentScore = sentimentScoreService.findById(id);
        return sentimentScore.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{source}/{articleId}")
    public ResponseEntity<SentimentScore> saveSentimentScore(@PathVariable String source, @PathVariable String articleId) {
        String baseUrl = "http://localhost:8080"; // Replace with the actual base URL or service discovery logic
        String url = String.format("%s/%s/get-sentiment-score/%s", baseUrl, source, articleId);

        logger.info("url: "+url);

        // Make the REST call to the specific article controller to get the sentiment score
//        ResponseEntity<Double> response = restTemplate.getForEntity(url, Double.class);
        ResponseEntity<SentimentScore> response = restTemplate.getForEntity(url, SentimentScore.class);
        sentimentScoreService.save(response.getBody());
        return response;
    }

    @PostMapping("/{source}")
    public ResponseEntity<SentimentScore> saveAllSentimentScoreForSource(@PathVariable String source) {
        String baseUrl = "http://localhost:8080"; // Replace with the actual base URL or service discovery logic
        String url = String.format("%s/%s/get-sentiment-score-for-all", baseUrl, source);
        logger.info("url: "+url);
        // Make the REST call to the specific article controller to get the sentiment score
//        ResponseEntity<Double> response = restTemplate.getForEntity(url, Double.class);
        ResponseEntity<SentimentScore> response = restTemplate.getForEntity(url, SentimentScore.class);
        sentimentScoreService.save(response.getBody());
        return response;
    }

    @PostMapping("/all")
    public String saveSentimentScoreForAll(@RequestParam(value = "numberOfSources", required = false, defaultValue = "-1") int numberOfSources) {
        String baseUrl = "http://localhost:8080"; // Replace with the actual base URL or service discovery logic
        String[] urls_orig  = {"wash-post-econo-articles",
                          "wsj-central-banking-articles",
                          "wsj-consumer-articles",
                          "wsj-earnings-articles",
                          //"wsj-earnings-more-articles",
                          "wsj-global-economy-articles",
                          "wsj-housing-articles",
                          "wsj-job-articles",
                          "wsj-trade-articles"};
        String[] urls = null;
        if(numberOfSources > 0) {
            urls = new String[numberOfSources];
            for(int i=0; i<numberOfSources; i++) {
                urls[i] = urls_orig[i];
            }
        }else{
            urls = urls_orig;
        }

        for(String eachsource:urls) {
            String url = String.format("%s/%s/get-sentiment-score-for-all", baseUrl, eachsource);
            logger.info("url: "+url);

            // Make the REST call to the specific article controller to get the sentiment score
            //ResponseEntity<Double> response = restTemplate.getForEntity(url, Double.class);
            ResponseEntity<List> response = restTemplate.getForEntity(url, List.class);

            List<SentimentScore> sentimentScoreList = Util.convertToSentimentScoreList(response.getBody());
            this.saveAll(sentimentScoreList);
        }
        return "all done";
    }

    //give me code to call all load-csv api to load all csv data for each controller
    @PostMapping("/load-all-csv")
    public String loadCsvData() {
        String baseUrl = "http://localhost:8080"; // Replace with the actual base URL or service discovery logic
        String[] urls  = {"wash-post-econo-articles",
                "wsj-central-banking-articles",
                "wsj-consumer-articles",
                "wsj-earnings-articles",
                "wsj-global-economy-articles",
                "wsj-housing-articles",
                "wsj-job-articles",
                "wsj-trade-articles"
        };

        for(String eachsource:urls) {
            String url = String.format("%s/%s/load-csv", baseUrl, eachsource);
            logger.info("url: "+url);

            // Make the REST call to the specific article controller to get the sentiment score
            //ResponseEntity<Double> response = restTemplate.getForEntity(url, Double.class);
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        }

        //give me code to call all cleansing api to cleansing all csv data for each controller.  This is POST call
        for(String eachsource:urls) {
            String url = String.format("%s/%s/cleansing", baseUrl, eachsource);
            logger.info("url: "+url);

            // Make the REST call to the specific article controller to get the sentiment score
            //ResponseEntity<Double> response = restTemplate.getForEntity(url, Double.class);
            try {
                ResponseEntity<String> response = restTemplate.postForEntity(url, null, String.class);
            }catch (Exception e){
                logger.info("error: "+"url: "+url+" ");
                logger.error("error: "+e.getMessage());
            }
        }


        return "all done";
    }

    public void saveAll(List<SentimentScore> sentimentScores) {
        sentimentScoreService.saveAll(sentimentScores);
    }


    private String getUniqueIdentifierForArticle(String source, String articleId) {
        // Implement the logic to retrieve the unique identifier for the article.
        // This might involve making another REST call or looking up in a database.
        // For example:
        // String uniqueUrl = baseUrl + "/unique-identifier/" + source + "/" + articleId;
        // return restTemplate.getForObject(uniqueUrl, String.class);
        return articleId; // Placeholder return statement
    }

}
