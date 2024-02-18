package com.cool.app.newssentimentanalysis.service;

import com.cool.app.newssentimentanalysis.sentiment.MySentiment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.Future;

@Service
public class SentimentAnalysisService {

    Logger logger = LoggerFactory.getLogger(SentimentAnalysisService.class);
    private Future<?> futureTask;

    @Autowired
    MySentiment mySentiment;


    @Async
    public Future<String> runSentimentAnalysis() {
        // Your sentiment analysis logic here
        logger.info("Sentiment analysis started...");

        // Simulate long-running task
        try {
            // This loop is just a placeholder for the real sentiment analysis task
            for (int i = 0; i < 10; i++) {
                if (Thread.currentThread().isInterrupted()) {
                    throw new InterruptedException("Sentiment analysis task was cancelled");
                }
                Thread.sleep(1000); // Placeholder for a segment of a long-running task
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return AsyncResult.forExecutionException(e);
        }

        logger.info("Sentiment analysis completed.");
        return AsyncResult.forValue("Completed");
    }

    public void stopSentimentAnalysis() {
        if (futureTask != null) {
            futureTask.cancel(true);
        }
    }

    public void setFutureTask(Future<?> futureTask) {
        this.futureTask = futureTask;
    }
}


