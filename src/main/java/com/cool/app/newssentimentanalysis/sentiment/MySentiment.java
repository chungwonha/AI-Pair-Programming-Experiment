package com.cool.app.newssentimentanalysis.sentiment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Properties;

import com.cool.app.newssentimentanalysis.entity.AnalyticalResults;
@Service @Data
public class MySentiment {
    Logger logger = LoggerFactory.getLogger(MySentiment.class);

    private StanfordCoreNLP pipeline;

    public MySentiment(){
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
        this.pipeline = new StanfordCoreNLP(props);
    }
    public AnalyticalResults run(String articleId, String text) throws SentimentAnalysisException {
        // Set up Stanford CoreNLP pipeline
//        Properties props = new Properties();
//        props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
//        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        // Text to be analyzed
        //String text = "I love this movie! It's so amazing!";


        // Create an Annotation object
        Annotation annotation = new Annotation(text);

        // Perform sentiment analysis
        pipeline.annotate(annotation);

        // Get the sentiment of each sentence
        HashMap<String,String> sentimentalAnalysiResults = new HashMap<>();
        HashMap<String,Integer> sentimentCounts = new HashMap<>();
        sentimentCounts.put("Positive",0);
        sentimentCounts.put("Neutral",0);
        sentimentCounts.put("Negative",0);
        sentimentCounts.put("Very positive",0);
        sentimentCounts.put("Very negative",0);

        for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
            String sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
            logger.info("Sentiment: " + sentiment);
            sentimentalAnalysiResults.put(sentence.toString(),sentiment);
            sentimentCounts.put(sentiment,sentimentCounts.get(sentiment)+1);
//            sentimentList.add(sentiment);
        }
        logger.info("Positive: "+sentimentCounts.get("Positive"));
        logger.info("Neutral: "+sentimentCounts.get("Neutral"));
        logger.info("Negative: "+sentimentCounts.get("Negative"));
        logger.info("Very positive: "+sentimentCounts.get("Very positive"));
        logger.info("Very negative: "+sentimentCounts.get("Very negative"));

        String jsonString = convertHashMapToStringJson(sentimentalAnalysiResults);
        AnalyticalResults analyticalResults = new AnalyticalResults();
        analyticalResults.setArticleId(articleId);
        analyticalResults.setAnalyticalResults(jsonString);

        analyticalResults.setPositiveCounts(sentimentCounts.get("Positive"));
        analyticalResults.setNeutralCounts(sentimentCounts.get("Neutral"));
        analyticalResults.setNegativeCounts(sentimentCounts.get("Negative"));
        analyticalResults.setVeryPositiveCounts(sentimentCounts.get("Very positive"));
        analyticalResults.setVeryNegativeCounts(sentimentCounts.get("Very negative"));
        PandNandScore pandNandScore = this.getPositiveScore(sentimentCounts);
        analyticalResults.setP_n(pandNandScore.P_N);
        logger.info("P_N: "+pandNandScore.P_N);
        analyticalResults.setScore(pandNandScore.score);
        logger.info("score: "+pandNandScore.score);
        return analyticalResults;//sentimentalAnalysiResults;//sentimentList;
    }

    public String convertHashMapToStringJson(HashMap<String,String> hashMap){
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            // Convert HashMap to JSON
            String json = objectMapper.writeValueAsString(hashMap);

            logger.info("JSON: " + json);
            return json;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "";
    }

    public AnalyticalResults runNYTAnalysis(Long id, String article){
        try {
            return this.run(id.toString(),article);
        } catch (SentimentAnalysisException e) {
            throw new RuntimeException(e);
        }
    }

    /*

            pos/(pos+neu+neg)
            neu/(pos+neu+neg)
            neg/(pos+neu+neg)

     */

    public PandNandScore getPositiveScore(HashMap<String,Integer> sentimentCounts){
        PandNandScore pandNandScore = new PandNandScore();

        double vpos = sentimentCounts.get("Very positive");
        double pos  = sentimentCounts.get("Positive");
        double neu  = sentimentCounts.get("Neutral");
        double neg  = sentimentCounts.get("Negative");
        double vneg = sentimentCounts.get("Very negative");

        double vp_score = vpos/(vpos+pos+neu+neg+vneg);
        double p_score = pos/(vpos+pos+neu+neg+vneg);
        double neu_score = neu/(vpos+pos+neu+neg+vneg);
        double n_score = neg/(vpos+pos+neu+neg+vneg);
        double vn_score = vneg/(vpos+pos+neu+neg+vneg);

        DecimalFormat df = new DecimalFormat("#.##########");
        String vp_formattedNumber = df.format(vp_score);
        String p_formattedNumber = df.format(p_score);
        String neu_formattedNumber = df.format(neu_score);
        String n_formattedNumber = df.format(n_score);
        String vn_formattedNumber = df.format(vn_score);

        logger.info(vp_score+", "+p_score+", "+neu_score+", "+n_score+", "+vn_score);
        logger.info(vp_formattedNumber+", "+p_formattedNumber+", "+neu_formattedNumber+", "+n_formattedNumber+", "+vn_formattedNumber);
        double[] numbers = {vp_score, p_score, neu_score, n_score, vn_score};

        double maxNumber = numbers[0];
        int maxIndex = 0;

        for (int i = 1; i < numbers.length; i++) {
            if (numbers[i] > maxNumber) {
                maxNumber = numbers[i];
                maxIndex = i;
            }
        }

        if(maxIndex == 0){
            pandNandScore.score=vp_score;
            pandNandScore.P_N="VPOS";
        }else if(maxIndex == 1){
            pandNandScore.score=p_score;
            pandNandScore.P_N="POS";
        }else if(maxIndex == 2){
            pandNandScore.score=neu_score;
            pandNandScore.P_N="NEU";
        }else if(maxIndex == 3){
            pandNandScore.score=n_score;
            pandNandScore.P_N="NEG";
        }else if(maxIndex == 4){
            pandNandScore.score=vn_score;
            pandNandScore.P_N="VNEG";
        }
        return pandNandScore;
    }

    class PandNandScore {
        String P_N;
        double score;


    }


}
