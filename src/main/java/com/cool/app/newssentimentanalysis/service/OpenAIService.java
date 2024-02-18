package com.cool.app.newssentimentanalysis.service;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Collections;

@Service
public class OpenAIService {
    Logger logger = org.slf4j.LoggerFactory.getLogger(OpenAIService.class);
    private ChatResponseService chatResponseService;

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.api.url}")
    private String apiUrl;

    @Value("${openai.api.chat.newsarticlesummary.prompt}")
    private String newsArticleSummaryPrompt;

    private final RestTemplate restTemplate;

    public OpenAIService(RestTemplate restTemplate, ChatResponseService chatResponseService) {
        this.restTemplate = restTemplate;
        this.chatResponseService = chatResponseService;
    }
    public void chatForNewsArticleSummary(String newsarticle,String uniqueIdentifier) {
        String s = getChatResponse(newsArticleSummaryPrompt + " this is the article "+newsarticle);
        logger.info("Summary returned -> : "+s);
        try {
            chatResponseService.saveChatResponse(s,uniqueIdentifier);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
    public String getChatResponse(String prompt) {
        String url = this.apiUrl;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("Authorization", "Bearer " + apiKey);

        String requestJson = "{\"model\": \"gpt-4\", \"messages\": [{\"role\": \"user\", \"content\": \"" + prompt + "\"}]}";

        HttpEntity<String> request = new HttpEntity<>(requestJson, headers);
        logger.info("url: "+url);
        logger.info("request: "+request);
        logger.info("requestJson: "+requestJson);

        return restTemplate.postForObject(url, request, String.class);
    }
}
