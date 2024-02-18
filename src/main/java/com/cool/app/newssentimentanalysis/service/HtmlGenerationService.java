package com.cool.app.newssentimentanalysis.service;

import com.cool.app.newssentimentanalysis.entity.SentimentScore;
import org.springframework.beans.factory.annotation.Autowired;


import java.util.ArrayList;
import java.util.List;

public class HtmlGenerationService {
    // #need a method to read data from sentiment_table and generate the html to draw graph of sentiment score with published dates

    private SentimentScoreService sentimentScoreService;
    @Autowired
    public HtmlGenerationService(SentimentScoreService sentimentScoreService) {
        this.sentimentScoreService = sentimentScoreService;
    }
    public String generateHtml() {
        //#need to call sentimentScoreService to get the data as List from sentiment_table
         List<SentimentScore> sentiment_table = sentimentScoreService.findAll();
         //#give me html code to draw the graph of sentiment score with published dates by looping through the List
        List<String> publishedDates = new ArrayList<>();
        List<Double> sentimentScores = new ArrayList<>();
        for (SentimentScore sentimentScore : sentiment_table) {
            publishedDates.add(sentimentScore.getPublishedDate());
            sentimentScores.add(sentimentScore.getSentimentScore());
            //#need the html cdoe to dynamically create and include the values of publishedDates and sentimentScores as x and y axis by looping through the List of publishedDates and sentimentScores
            //#as an example, the html code to draw a graph with x and y axis as 1,2,3,4 and 10,15,13,17 is as follows:
            // <html>
            // <head>
            // <script src="https://cdn.plot.ly/plotly-latest.min.js"></script>
            // </head>
            // <body>
            // <div id="myDiv"><!-- Plotly chart will be drawn inside this DIV --></div>
            // <script>
            // var trace1 = {
            //   x: [1, 2, 3, 4],
            //   y: [10, 15, 13, 17],
            //   type: 'scatter'
            // };
            // var trace2 = {
            //   x: [1, 2, 3, 4],
            //   y: [16, 5, 11, 9],
            //   type: 'scatter'
            // };
            // var data = [trace1, trace2];
            // Plotly.newPlot('myDiv', data);
            // </script>
            // </body>
            // </html>
            //#need to replace the values of x and y axis with the values of publishedDates and sentimentScores
            //#concanate values from sentimentScore.getSentimentScore() and sentimentScore.getPublishedDate() to the html code to draw the graph on x: [1, 2, 3, 4] and y: [10, 15, 13, 17]

            sentimentScores.add(sentimentScore.getSentimentScore());
            publishedDates.add(sentimentScore.getPublishedDate());



        }
        sentimentScores.add(0.0);
        publishedDates.add("2020-01-01");

        String graph = "<html><head><script src=\"https://cdn.plot.ly/plotly-latest.min.js\"></script></head><body><div id=\"myDiv\"><!-- Plotly chart will be drawn inside this DIV --></div><script>var trace1 = {x: [1, 2, 3, 4],y: [10, 15, 13, 17],type: 'scatter'};var trace2 = {x: [1, 2, 3, 4],y: [16, 5, 11, 9],type: 'scatter'};var data = [trace1, trace2];Plotly.newPlot('myDiv', data);</script></body></html>";

        return graph;
    }



}
