package com.cool.app.newssentimentanalysis.service;

import com.cool.app.newssentimentanalysis.controller.Util;
import com.cool.app.newssentimentanalysis.entity.SentimentScore;
import com.cool.app.newssentimentanalysis.entity.openai.ChatResponse;
import com.cool.app.newssentimentanalysis.entity.openai.Choice;
import com.cool.app.newssentimentanalysis.entity.openai.Message;
import com.cool.app.newssentimentanalysis.repository.openai.ChatResponseRepository;
import com.cool.app.newssentimentanalysis.sentiment.SentimentAnalyzer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;


@Service
public abstract class GenericArticleService<E, R extends JpaRepository<E, Integer>> {
    Logger logger = LoggerFactory.getLogger(GenericArticleService.class.getName());

    protected final R articleRepository;
    protected final SentimentAnalyzer sentimentAnalyzer;

    @Autowired
    ChatResponseService chatResponseService;

    @Autowired
    S3Service s3Service;
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    OpenAIService openAIService;

    @Autowired
    ChatResponseRepository chatResponseRepository;
    @Autowired
    protected SentimentScoreService sentimentScoreService;

    @Value("${openai.api.response.html-path}")
    private String openaiResponseHtmlOutputPath;

    @Autowired
    public GenericArticleService(R articleRepository, SentimentAnalyzer sentimentAnalyzer) {
        this.articleRepository = articleRepository;
        this.sentimentAnalyzer = sentimentAnalyzer;
    }

    public Optional<E> getArticleById(int id) {
        return articleRepository.findById(id);
    }

    public Optional<E> getArticleByIdWithSentimentScore(int id, java.util.function.Function<E, String> getTextFunction) {
        Optional<E> articleEntity = articleRepository.findById(id);

        articleEntity.ifPresent(article -> {
            String articleText = getTextFunction.apply(article);
            double sentiment = sentimentAnalyzer.getAggregatedSentiment(articleText);
            logger.info("Sentiment of the article: " + sentiment);
        });

        return articleEntity;
    }

    public Optional<SentimentScore> getSentimentScore(
            int id,
            Function<E, String> getTextFunction,
            Function<E, String> getUniqueIdentifierFunction,
            Function<E, String> getPublishedDateFunction,
            Function<E, String> getArticleTitleFunction,
            Function<String, String> convertDateFunction,
            Function<E, String> getArticleTypeFunction,
            Function<E,String> getArticleSourceFunction) {

        Optional<E> articleEntity = articleRepository.findById(id);


        if (articleEntity.isPresent()) {
            E article = articleEntity.get();
            String articleText = getTextFunction.apply(article);
            Optional<SentimentScore> existingScore = sentimentScoreService.findByUniqueIdentifier(getUniqueIdentifierFunction.apply(article));
            if(!existingScore.isPresent()) {
                logger.info("Calculate the sentiment score for "+getUniqueIdentifierFunction.apply(article));
                double sentiment = sentimentAnalyzer.getAggregatedSentiment(articleText);

                SentimentScore sentimentScore = new SentimentScore();
                sentimentScore.setUniqueIdentifier(getUniqueIdentifierFunction.apply(article));
                sentimentScore.setSentimentScore(sentiment);
                sentimentScore.setPublishedDate(convertDateFunction.apply(getPublishedDateFunction.apply(article)));
                sentimentScore.setArticleTitle(getArticleTitleFunction.apply(article));
                sentimentScore.setArticleType(getArticleTypeFunction.apply(article));
                sentimentScore.setArticleSource(getArticleSourceFunction.apply(article));

                // Save the SentimentScore entity if needed
                // sentimentScoreRepository.save(sentimentScore);

                return Optional.of(sentimentScore);
            }else{
                logger.info("Sentiment score already existing for "+getUniqueIdentifierFunction.apply(article));
                return existingScore;
            }
        }
        return Optional.empty();
    }

    public List<SentimentScore> getSentimentScoreForAll(
            Function<E, String> getTextFunction,
            Function<E, String> getUniqueIdentifierFunction,
            Function<E, String> getPublishedDateFunction,
            Function<E, String> getArticleTitleFunction,
            Function<String, String> convertDateFunction,
            Function<E, String> getArticleTypeFunction,
            Function<E,String> getArticleSourceFunction) {

        List<E> articleEntity = articleRepository.findAll();
        List<SentimentScore> sentimentScores = new ArrayList<>();

        if (articleEntity.size()>0) {
            for(E article: articleEntity) {
                Optional<SentimentScore> existingScore = sentimentScoreService.findByUniqueIdentifier(getUniqueIdentifierFunction.apply(article));

                if(!existingScore.isPresent()) {
                    logger.info("Calculate the sentiment score for "+getUniqueIdentifierFunction.apply(article));
                    String articleText = getTextFunction.apply(article);
                    if(articleText == null || articleText.isEmpty()) {
                        logger.info("Article text is empty for "+getUniqueIdentifierFunction.apply(article));
                    }else{
                        double sentiment = sentimentAnalyzer.getAggregatedSentiment(articleText);

                        SentimentScore sentimentScore = new SentimentScore();
                        sentimentScore.setUniqueIdentifier(getUniqueIdentifierFunction.apply(article));
                        sentimentScore.setSentimentScore(sentiment);
                        sentimentScore.setPublishedDate(convertDateFunction.apply(getPublishedDateFunction.apply(article)));
                        sentimentScore.setArticleTitle(getArticleTitleFunction.apply(article));
                        sentimentScore.setArticleType(getArticleTypeFunction.apply(article));
                        sentimentScore.setArticleSource(getArticleSourceFunction.apply(article));

                        // Save the SentimentScore entity if needed
                        // sentimentScoreRepository.save(sentimentScore);

                        sentimentScores.add(sentimentScore);
                    }

                }else{
                    logger.info("Sentiment score already existing for "+getUniqueIdentifierFunction.apply(article));
                    sentimentScores.add(existingScore.get());
                }
            }
            return sentimentScores;
        }
        return new ArrayList<>();
    }

    public abstract Optional<SentimentScore> getSentimentScore(int id);
    public Optional<Double> getSentimentScoreById(int id, Function<E, String> getTextFunction) {

        return articleRepository.findById(id).map(article->{
            String articleText = getTextFunction.apply(article);
            double sentiment = sentimentAnalyzer.getAggregatedSentiment(articleText);
            return sentiment;
        });
    }

    public void chatWithOpenAIForNewsArticleSummaryAndSave(Function<E,String> getArticle,
                                                           Function<E,String> getUniqueIdentifier) {
        List<E> articleEntity = articleRepository.findAll();

        if (articleEntity.size()>0) {
            for(E article: articleEntity) {
                Optional<ChatResponse> chatResponse = chatResponseRepository.findByUniqueIdentifier(getUniqueIdentifier.apply(article));
                if(!chatResponse.isPresent()) {
                    logger.info("get Article by "+getUniqueIdentifier.apply(article));
                    String articleText = Util.removeExtraSpaces(getArticle.apply(article));
                    if(articleText == null || articleText.isEmpty()) {
                        logger.info("Article text is empty for "+getUniqueIdentifier.apply(article));
                    }else{
                        articleText = Util.escapeDoubleQuotes(articleText);
                        openAIService.chatForNewsArticleSummary(articleText, getUniqueIdentifier.apply(article));
//                        logger.info("jsonResponse from ChatGPT: "+json);
//                        try {
//                            chatResponseService.saveChatResponse(json, getUniqueIdentifier.apply(article));
//                        }catch(Exception e) {
//                            logger.error("Error saving chat response", e);
//                        }
                    }

                }else{
                    logger.info("Chat Response for the Summary already existing for "+getUniqueIdentifier.apply(article));
                }
            }
        }

    }


        @Transactional
        public void generateHtmlFromJsonAndUploadToS3(Function<String,String> findArticleTitleByArticleLinksHref,
                                                      Function<E, String> getArticleTitle) {
//        String json = "{ \"summary\": \"Following the addition of 336,000 jobs in September, signs of an accelerating U.S. economy have sparked a bond market selloff, with longer-term borrowing rates reaching 16-year highs. The strong jobs report has lowered the likelihood of a recession and subsequent Fed rate cuts. Investors reacted nervously initially but the markets recovered later. The jobs growth significantly surpassed economic forecasts, despite rising oil prices, interest rates, and inflation. This robust jobs growth, driven partly by robust consumer spending, could complicate matters for the Federal Reserve, which held its key interest rate steady last month.\", \"sentiment_score\": 0.7, \"rap_title\": \"Bonds on the Run, Jobs Having Fun\", \"rap_lyrics\": \"Yo, it's the US economy on a ride,\\nBurstin' jobs, investors can't hide.\\nBond market sellin' off, rates hit the sky, \\nRecession fears sayin' bye-bye!\\nInflation hittin’, oil prices liftin',\\nBut job growth ain't shiftin'.\\nFed's got decisions, rates on a mission,\\nEconomy in transition, that's the vision!\", \"labels\": \"US economy, Job growth, Bond market, Federal Reserve, Interest rates, Recession, Inflation, Oil Prices\", \"cartoon\": \"Cartoon of the U.S economy as a speeding train, filled with workers, charging past different economics roadblocks such as 'recession', 'inflation', 'Fed rate cuts'. The Federal Reserve is shown as a train conductor, with thought bubbles contemplating next moves.\" }";
        chatResponseService.getAllChatResponses().stream().forEach(chatResponse -> {
            List<Choice> choices= chatResponse.getChoices();
            choices.stream().forEach(choice -> {
            Message message = choice.getMessage();
            String jsonContent = message.getContent();
                if(message.getS3etag()==null || message.getS3etag().isEmpty()) {
                    try {
                        String articleTitle = findArticleTitleByArticleLinksHref.apply(chatResponse.getUniqueIdentifier());
                        logger.info("articleTitle before senitization: "+articleTitle);

                        articleTitle = Util.sanitizeFileName(articleTitle);
                        logger.info("articleTitle after senitization: "+articleTitle);

                        String htmlContent = jsonToHtml(jsonContent);

                        String key = articleTitle+".html"; // e.g., "myfolder/myfile.html"

                        PutObjectResponse response = s3Service.uploadFile(key, htmlContent);
                        logger.info("html generated: " + htmlContent);
                        logger.info("response: " + response);
                        logger.info("response.eTag(): " + response.eTag());

                        message.setS3etag(response.eTag());
                        message.setS3Key(key);

                        chatResponseService.saveMessage(message);
                    } catch (Exception e) {
                        logger.error("error while generating html and upload for: " + chatResponse.getUniqueIdentifier());
                        logger.error("Error generating html and uploading to S3", e);
                    }
                }else{
                    logger.info("html already generated and uploaded for: "+chatResponse.getUniqueIdentifier());
                }
            });
        });
    }


    private String jsonToHtml(String json) {
        StringBuilder htmlBuilder = new StringBuilder();
        htmlBuilder.append("<html><head><style>h1 { font-size: 24px; font-weight: bold; }</style></head><body>");

        try {
            JsonNode jsonNode = objectMapper.readTree(json);
            jsonNode.fields().forEachRemaining(field -> {
                String key = field.getKey();
                String value = field.getValue().asText();

                htmlBuilder.append("<h1>").append(capitalizeFirstLetter(key.replace("_", " "))).append("</h1>");
                htmlBuilder.append("<p>").append(value).append("</p>");
            });
        } catch (IOException e) {
            logger.error("Error parsing JSON", e);
            return "Error parsing JSON";
        }

        htmlBuilder.append("</body></html>");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(this.openaiResponseHtmlOutputPath + "/test123.html"))) {
            writer.write(htmlBuilder.toString());
        }catch (IOException e) {
            logger.error("Error writing HTML to file", e);
            return "Error writing HTML to file";
        }

        return htmlBuilder.toString();
    }

    private String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }
    public abstract List<SentimentScore> getSentimentScoreForAll();

    public SentimentScoreService getSentimentScoreService() {
        return sentimentScoreService;
    }

    public void setSentimentScoreService(SentimentScoreService sentimentScoreService) {
        this.sentimentScoreService = sentimentScoreService;
    }

}
