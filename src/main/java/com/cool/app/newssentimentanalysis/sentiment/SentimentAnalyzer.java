package com.cool.app.newssentimentanalysis.sentiment;

import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Properties;

@Service
public class SentimentAnalyzer {
    private StanfordCoreNLP pipeline;

    public SentimentAnalyzer() {
        // Set up pipeline properties
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");

        // Build pipeline
        this.pipeline = new StanfordCoreNLP(props);
    }

    public String getSentiment(String text) {
        // Create an empty Annotation just with the given text
        Annotation annotation = new Annotation(text);

        // Run all Annotators on this text
        this.pipeline.annotate(annotation);

        // Iterate over all of the sentences found
        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
        StringBuilder sb = new StringBuilder();

        for(CoreMap sentence: sentences) {
            // Retrieve the sentiment analysis of the current sentence
            String sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
            sb.append(sentiment).append("\n");
        }

        return sb.toString();
    }
    /*

    Use average sentiment scoring or mean sentiment scoring. This method calculates the overall sentiment of a document by:

    Classifying the sentiment of each sentence within the document.
    Assigning a numerical value to each classified sentiment.
    Averaging these numerical values to derive a single sentiment score that represents the entire document.

    In this methodology, each sentence is given equal weight,
    and the final score is intended to reflect the general sentiment of the text as a whole.
    It assumes that the sentiment of the entire document can be approximated by averaging the sentiment scores of its constituent sentences.
     */
    public double getAggregatedSentiment(String documentText) {
        CoreDocument document = new CoreDocument(documentText);
        pipeline.annotate(document);
        double totalSentiment = 0;
        int sentencesCount = document.sentences().size();

        for (CoreMap sentence : document.annotation().get(CoreAnnotations.SentencesAnnotation.class)) {
            String sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
            totalSentiment += convertSentimentToNumeric(sentiment);
        }

        // Calculate average sentiment score
        return sentencesCount > 0 ? totalSentiment / sentencesCount : 0;
    }

    private int convertSentimentToNumeric(String sentiment) {
        switch (sentiment) {
            case "Very negative":
                return 0;
            case "Negative":
                return 1;
            case "Neutral":
                return 2;
            case "Positive":
                return 3;
            case "Very positive":
                return 4;
            default:
                return 2; // Default to neutral if unknown
        }
    }
}
