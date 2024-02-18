package com.cool.app.newssentimentanalysis.controller;

import com.cool.app.newssentimentanalysis.entity.SentimentScore;
import com.cool.app.newssentimentanalysis.service.SentimentScoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/html-graph")
public class HtmlGraphController {
    private static final Logger logger = LoggerFactory.getLogger(HtmlGraphController.class);

    private final SentimentScoreService sentimentScoreService;
    @Value("${file.graph-file.path}")
    private String graphHtmlOutputPath;

    @Autowired
    public HtmlGraphController(SentimentScoreService sentimentScoreService) {
        this.sentimentScoreService = sentimentScoreService;
    }

    @GetMapping("/generate-graph")
    public void generateGraphHtml() throws IOException {
        List<SentimentScore> scores = sentimentScoreService.findAll();

        if (scores.isEmpty()) {
            logger.info("No sentiment scores available to generate the graph.");
            return;
        }

        Map<String, List<SentimentScore>> groupedScores = scores.stream()
                .collect(Collectors.groupingBy(score -> score.getArticleSource() + " - " + score.getArticleType()));

        StringBuilder dataBuilder = new StringBuilder();
        dataBuilder.append("[");

        // Generate a dataset for each group
        for (Map.Entry<String, List<SentimentScore>> entry : groupedScores.entrySet()) {
            dataBuilder.append("{")
                    .append("label: '").append(entry.getKey()).append("',")
                    .append("borderColor: getRandomColor(),") // You'll define getRandomColor() in JavaScript
                    .append("data: [");

            for (SentimentScore score : entry.getValue()) {
                dataBuilder.append("{x: new Date('")
                        .append(score.getPublishedDate())
                        .append("'), y: ")
                        .append(score.getSentimentScore())
                        .append("},");
            }

            // Remove the last comma and close the dataset
            if (!entry.getValue().isEmpty()) {
                dataBuilder.setLength(dataBuilder.length() - 1);
            }
            dataBuilder.append("]},");
        }

        // Remove the last comma and close the datasets array
        dataBuilder.setLength(dataBuilder.length() - 1);
        dataBuilder.append("]");

        String htmlContent = generateHtmlContent(dataBuilder.toString());

        // Write to file...
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(this.graphHtmlOutputPath + "/sentiment_graph.html"))) {
            writer.write(htmlContent);
        }
    }
    @GetMapping("/generate-graph1")
    public void generateGraphHtml1() throws IOException {
        List<SentimentScore> scores = sentimentScoreService.findAll(); // Assuming a findAll() method in your service

        // Prepare data for the graph (this part is simplified and may vary depending on your SentimentScore entity)
        StringBuilder dataBuilder = new StringBuilder();
        dataBuilder.append("[");

        scores.forEach(score -> dataBuilder
                .append("{x: new Date('")
                .append(score.getPublishedDate()) // Assuming you have a getPublishedDate() method
                .append("'), y: ")
                .append(score.getSentimentScore())
                .append("},"));

// Remove the last comma to comply with correct JSON format
        if (scores.size() > 0) {
            dataBuilder.setLength(dataBuilder.length() - 1);
        }

        dataBuilder.append("]");

        String graphData = dataBuilder.toString();
// Now you can pass graphData to your generateGraph method


        String htmlContent = generateHtmlContent(dataBuilder.toString());

        // Write to file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(this.graphHtmlOutputPath + "/sentiment_graph.html"))) {
            writer.write(htmlContent);
        }
    }

    private String generateHtmlContent(String graphData) {
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <title>Sentiment Graph</title>\n" +
                "    <script src=\"https://cdn.jsdelivr.net/npm/chart.js@2.9.4\"></script>\n" +
                "    <script src='https://cdn.jsdelivr.net/npm/chartjs-adapter-date-fns'></script>\n" +
                "</head>\n" +
                "<script>\n" +
                "    function getRandomColor() {\n" +
                "        var letters = '0123456789ABCDEF';\n" +
                "        var color = '#';\n" +
                "        for (var i = 0; i < 6; i++) {\n" +
                "            color += letters[Math.floor(Math.random() * 16)];\n" +
                "        }\n" +
                "        return color;\n" +
                "    }\n" +
                "</script>\n"+
                "<body>\n" +
                "    <canvas id='sentimentGraph' width='800' height='400'></canvas>\n" +
                "    <script>\n" +
                "        var ctx = document.getElementById('sentimentGraph').getContext('2d');\n" +
                "        var chart = new Chart(ctx, {\n" +
                "            type: 'line',\n" +
                "            data: {\n" +
                "                datasets: [{\n" +
                "                    label: 'Sentiment Score',\n" +
                "                    data: " + graphData + "\n" +
                "                }]\n" +
                "            },\n" +
                "            options: {\n" +
                "                scales: {\n" +
                "                    xAxes: [{\n" +
                "                        type: 'time',\n" +
                "                        time: {\n" +
                "                            unit: 'day',\n" +
                "                            tooltipFormat: 'yyyy-MM-dd',\n" +
                "                            displayFormats: {\n" +
                "                                day: 'yyyy-MM-dd'\n" +
                "                            }\n" +
                "                        }\n" +
                "                    }],\n" +
                "                    yAxes: [{\n" +
                "                        scaleLabel: {\n" +
                "                            display: true,\n" +
                "                            labelString: 'Sentiment Score'\n" +
                "                        }\n" +
                "                    }]\n" +
                "                }\n" +
                "            }\n" +
                "        });\n" +
                "    </script>\n" +
                "</body>\n" +
                "</html>";
    }



}
